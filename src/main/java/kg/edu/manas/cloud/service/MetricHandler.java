package kg.edu.manas.cloud.service;

import kg.edu.manas.cloud.config.MqttOutboundConfig;
import kg.edu.manas.cloud.model.cache.RedisCache;
import kg.edu.manas.cloud.model.data.enums.Level;
import kg.edu.manas.cloud.model.data.enums.MetricType;
import kg.edu.manas.cloud.model.data.enums.Range;
import kg.edu.manas.cloud.model.data.property.AlertTimingProperties;
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
import static kg.edu.manas.cloud.util.MetricUtil.isPriorityEqual;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricHandler {
    @Value("${application.emergency.mail}")
    private String emergencyMail;
    private final AlertTimingProperties timing;
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
        var index = metricType.toString() + deviceIdCipher;
        int age = customerService.getAge(deviceIdCipher);
        var ranges = List.of(Range.ALL, MetricUtil.getRange(age));
        Level level = getLevelForMetric(metricType,value,ranges);

        if (level.equals(Level.NORMAL)) {
            return;
        }

        log.debug("Processing metric. Index: {}", index);

        var alert = new AlertCacheRecord(level, System.currentTimeMillis(), 1, false);
        var alertOpt = redisCache.get(index);

        alertOpt.ifPresentOrElse(alertCache -> handleExistingAlert(metric, plainDeviceId, index, level, alert, alertCache),
                () -> handleNewAlert(metric, plainDeviceId, index, level, alert));
    }

    private Level getLevelForMetric(MetricType metricType, int value, List<Range> ranges) {
        return configService.findAll().get(metricType).stream()
                .filter(config -> ranges.contains(config.getRange()))
                .filter(config -> value >= config.getMin() && value <= config.getMax())
                .findFirst()
                .orElseThrow()
                .getLevel();
    }

    private void handleExistingAlert(Metric metric, String plainDeviceId, String index, Level level, AlertCacheRecord alert, Object alertCacheObj) {
        AlertCacheRecord alertCache = (AlertCacheRecord) alertCacheObj;
        if(!alertCache.isSent()) {
            if(isPriorityHigher(alert.getLevel(), alertCache.getLevel())) {
                if(level.equals(Level.EMERGENCY)) {
                    handleEmergencyAlert(metric, plainDeviceId, index, level, alertCache);
                }
                else {
                    announce(metric, alertCache.getLevel(), plainDeviceId);
                    redisCache.putWithTTL(index, alert, timing.getTtl());
                    log.debug("Sent alert from cache. New high priority alert replaced it");
                }
            }
            else if(isPriorityEqual(alert.getLevel(), alertCache.getLevel())) {
                log.debug("New alert priority is same");
                handleEqualPriorityAlert(metric, plainDeviceId, index, level, alertCache);
            }
        } else {
            log.debug("Blocking period");
        }
    }

    private void handleEqualPriorityAlert(Metric metric, String plainDeviceId, String index, Level level, AlertCacheRecord alertCache) {
        var waitingPeriod = Math.abs(System.currentTimeMillis() - alertCache.getTimestamp());

        if(waitingPeriod < (timing.getWaiting() * 60000L)) {
            long ttl = redisCache.getExpire(index);
            alertCache.setCount(alertCache.getCount() + 1);
            redisCache.putWithTTL(index, alertCache, ttl);
            log.debug("Waiting period hasn't expired yet. Incremented count");
        } else {
            log.debug("Waiting period has expired");
            if(alertCache.getCount() > (timing.getTotal() * timing.getThreshold())) {
                announce(metric, level, plainDeviceId);
                alertCache.setSent(true);
                redisCache.putWithTTL(index, alertCache, timing.getBlock());
                log.debug("Alert threshold has been reached. Alert has been sent. The blocking time is set");
            } else {
                redisCache.remove(index);
                log.debug("Alert threshold hasn't been reached. Alert is removed");
            }
        }
    }

    private void handleNewAlert(Metric metric, String plainDeviceId, String index, Level level, AlertCacheRecord alert) {
        if(level.equals(Level.EMERGENCY)) {
            handleEmergencyAlert(metric, plainDeviceId, index, level, alert);
        } else {
            redisCache.putWithTTL(index, alert, timing.getTtl());
            log.debug("Put new alert to cache");
        }
    }

    private void handleEmergencyAlert(Metric metric, String plainDeviceId, String index, Level level, AlertCacheRecord alert) {
        announce(metric, level, plainDeviceId);
        alert.setSent(true);
        redisCache.putWithTTL(index, alert, timing.getEBlock());
        log.debug("Sent emergency alert immediately");
    }

    private void announce(Metric metric, Level level, String plainDeviceId) {
        String metricName = MetricUtil.getMetricName(metric.getType());

        switch (level) {
            case NORMAL -> {}
            case WARNING -> mqttGateway.sendToMqtt(String.format(TOPIC_WARNING_MSG, metricName), "device/" + plainDeviceId + "/msg");
            case CRITICAL -> mqttGateway.sendToMqtt(String.format(TOPIC_CRITICAL_MSG, metricName), "device/" + plainDeviceId + "/msg");
            case EMERGENCY -> {
                var gps = metricRepository.findFirstByDeviceIdAndTypeOrderByTimestampDesc(metric.getDeviceId(), MetricType.GPS);
                String gpsValue = gps.map(Metric::getValue).orElse("_");
                String timestamp = gps.map(value -> DateTimeUtil.format(value.getTimestamp())).orElse("_");
                String user = customerService.getName(metric.getDeviceId());

                emailNotificationService.sendMessageAsync(new EmailMessageRecord(
                        emergencyMail,
                        HELP_SUB,
                        String.format(HELP_MSG, user, metricName, gpsValue, timestamp)
                ));
            }
        }
        log.warn("{}: {}: {}", level, metricName, metric.getValue());
    }
}
