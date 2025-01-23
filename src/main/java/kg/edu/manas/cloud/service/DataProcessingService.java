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
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static kg.edu.manas.cloud.util.MetricUtil.isPriorityHigher;
import static kg.edu.manas.cloud.util.MetricUtil.isPriorityLower;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataProcessingService {
    private final MqttOutboundConfig.MqttGateway mqttGateway;
    private final EmailNotificationService emailNotificationService;
    private final EncryptionService encryptionService;
    private final ConfigService configService;
    private final CustomerService customerService;
    private final MetricRepository metricRepository;
    private final RedisCache redisCache;

    public void process(Message<?> message) {
        String topic = Objects.requireNonNull(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC)).toString();
        String[] parts = topic.split("/");
        MetricType metricType = MetricUtil.getMetricType(parts[2]);
        String encryptedDeviceId = encryptionService.encrypt(parts[1]);
        var metric = Metric.builder()
                .type(metricType)
                .value(message.getPayload().toString())
                .timestamp(message.getHeaders().getTimestamp())
                .deviceId(encryptedDeviceId).build();
        int value = Integer.parseInt(metric.getValue());
        int age = customerService.getAge(encryptedDeviceId);
        var ranges = List.of(Range.ALL, MetricUtil.getRange(age));
        Level level = configService.findAll().get(metricType).stream()
                        .filter(config -> ranges.contains(config.getRange()))
                        .filter(config -> value >= config.getMin() && value <= config.getMax())
                        .findFirst().orElseThrow().getLevel();

        AlertCacheRecord alert = new AlertCacheRecord(metricType, level);
        var alertOpt = redisCache.get(metric.getDeviceId());
        if(alertOpt.isPresent()) {
            AlertCacheRecord alertCache = (AlertCacheRecord) alertOpt.get();
            if(alert.metric().equals(alertCache.metric())) {
                if(!isPriorityHigher(alert.level(), alertCache.level())) {
                    return;
                }
            } else {
                if(isPriorityLower(alert.level(), alertCache.level())) {
                    return;
                }
            }
        }
        redisCache.putWithTTL(metric.getDeviceId(), alert);
        announce(metric, level);
//        metricRepository.save(metric);
    }

    private void announce(Metric metric, Level level) {
        switch (level) {
            case NORMAL -> {}
            case WARNING -> mqttGateway.sendToMqtt("Warning: " + metric.getType() +" is out of the normal range. Please monitor closely.", "device/" + metric.getDeviceId() + "/msg");
            case CRITICAL -> {
                mqttGateway.sendToMqtt("Critical: " + metric.getType() + " is outside safe parameters. Immediate attention required.", "device/" + metric.getDeviceId() + "/msg");
                System.out.println("Send notification via web socket"); // to do
            }
            case EMERGENCY -> {
                Metric gps = metricRepository.findLastMetricByDeviceIdAndType(metric.getDeviceId(), MetricType.GPS);
                emailNotificationService.sendMessage(new EmailMessageRecord(
                        "2004.01035@manas.edu.kg",
                        "CALL FOR HELP",
                        metric.getType() + " has gone beyond acceptable limits. " +
                                "Detected at: " + DateTimeUtil.format(gps.getTimestamp()) +
                                // https://2gis.kg/search/geo/74.576216479763389,42.832382144897558
                                ". Location: https://2gis.kg/search/geo/" + gps.getValue()
                ));
            }
        }
    }
}
