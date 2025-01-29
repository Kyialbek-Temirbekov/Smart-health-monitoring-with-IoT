package kg.edu.manas.cloud.service;

import kg.edu.manas.cloud.config.MqttOutboundConfig;
import kg.edu.manas.cloud.model.cache.RedisCache;
import kg.edu.manas.cloud.model.data.enums.Level;
import kg.edu.manas.cloud.model.data.enums.MetricType;
import kg.edu.manas.cloud.model.data.enums.Range;
import kg.edu.manas.cloud.model.data.record.AlertCacheRecord;
import kg.edu.manas.cloud.model.data.record.EmailMessageRecord;
import kg.edu.manas.cloud.model.entity.Metric;
import kg.edu.manas.cloud.model.repository.MetricRepository;
import kg.edu.manas.cloud.util.DateTimeUtil;
import kg.edu.manas.cloud.util.MetricUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static kg.edu.manas.cloud.model.data.constants.Messages.*;
import static kg.edu.manas.cloud.util.MetricUtil.isPriorityHigher;
import static kg.edu.manas.cloud.util.MetricUtil.isPriorityLower;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricHandler {
    @Value("${application.emergency.mail}")
    private String emergencyMail;
    private final MqttOutboundConfig.MqttGateway mqttGateway;
    private final EmailNotificationService emailNotificationService;
    private final EncryptionService encryptionService;
    private final ConfigService configService;
    private final CustomerService customerService;
    private final MetricRepository metricRepository;
    private final RedisCache redisCache;

    public void handle(Message<?> message) {
        try {
            String topic = Objects.requireNonNull(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC)).toString();
            String[] parts = topic.split("/");
            MetricType metricType = MetricUtil.getMetricType(parts[2]);
            String deviceId = parts[1];

            if(metricType.equals(MetricType.UNKNOWN)) {
                return;
            }

            var metric = Metric.builder()
                    .type(metricType)
                    .value(message.getPayload().toString())
                    .timestamp(message.getHeaders().getTimestamp())
                    .deviceId(encryptionService.encrypt(deviceId)).build();

            switch (metricType) {
                case HEART_BEAT, SATURATION, AIR_QUALITY -> process(metric, deviceId);
                default -> {}
            }

            metricRepository.save(metric);
        } catch (Exception e) {
            log.error("Error processing metrics", e);
        }
    }

    private void process(Metric metric, String plainDeviceId) {
        String deviceIdCipher = metric.getDeviceId();
        var metricType = metric.getType();
        int value = Integer.parseInt(metric.getValue());
        int age = customerService.getAge(deviceIdCipher);
        var ranges = List.of(Range.ALL, MetricUtil.getRange(age));

        Level level = configService.findAll().get(metricType).stream()
                .filter(config -> ranges.contains(config.getRange()))
                .filter(config -> value >= config.getMin() && value <= config.getMax())
                .findFirst().orElseThrow().getLevel();

        var alert = new AlertCacheRecord(metricType, level);
        if(shouldAnnounce(alert, deviceIdCipher) && !level.equals(Level.NORMAL)) {
            redisCache.putWithTTL(deviceIdCipher, alert);
            announce(metric, level, plainDeviceId);
        }
    }

    private boolean shouldAnnounce(AlertCacheRecord alert, String deviceIdCipher) {
        var alertOpt = redisCache.get(deviceIdCipher);
        if(alertOpt.isPresent()) {
            AlertCacheRecord alertCache = (AlertCacheRecord) alertOpt.get();
            if(alert.metric().equals(alertCache.metric())) {
                return isPriorityHigher(alert.level(), alertCache.level());
            } else {
                return !isPriorityLower(alert.level(), alertCache.level());
            }
        }
        return true;
    }

    private void announce(Metric metric, Level level, String plainDeviceId) {
        String metricName = MetricUtil.getMetricName(metric.getType());

        switch (level) {
            case NORMAL -> {}
            case WARNING -> {
                mqttGateway.sendToMqtt(String.format(TOPIC_WARNING_MSG, metricName), "device/" + plainDeviceId + "/msg");
            }
            case CRITICAL -> {
                mqttGateway.sendToMqtt(String.format(TOPIC_CRITICAL_MSG, metricName), "device/" + plainDeviceId + "/msg");
            }
            case EMERGENCY -> {
                var gps = metricRepository.findFirstByDeviceIdAndTypeOrderByTimestampDesc(metric.getDeviceId(), MetricType.GPS);
                String gpsValue = gps.map(Metric::getValue).orElse("_");
                String timestamp = gps.map(value -> DateTimeUtil.format(value.getTimestamp())).orElse("_");

                emailNotificationService.sendMessage(new EmailMessageRecord(
                        emergencyMail,
                        HELP_SUB,
                        String.format(HELP_MSG, metricName, gpsValue, timestamp)
                ));
            }
        }
        log.warn("{}: {}: {}", level, metricName, metric.getValue());
    }
}
