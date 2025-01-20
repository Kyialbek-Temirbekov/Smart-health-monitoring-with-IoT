package kg.edu.manas.cloud.service;

import kg.edu.manas.cloud.date.enums.Level;
import kg.edu.manas.cloud.date.enums.MetricType;
import kg.edu.manas.cloud.date.enums.Range;
import kg.edu.manas.cloud.entity.Metric;
import kg.edu.manas.cloud.executor.Executor;
import kg.edu.manas.cloud.repository.MetricRepository;
import kg.edu.manas.cloud.util.MetricUtil;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class DataProcessingService {
    private final Map<String, Executor> executors;
    private final ConfigService configService;
    private final MetricRepository metricRepository;

    public DataProcessingService(Map<String, Executor> executors, ConfigService configService, MetricRepository metricRepository) {
        this.executors = executors;
        this.configService = configService;
        this.metricRepository = metricRepository;
    }

    public void process(Message<?> message) {
        String topic = Objects.requireNonNull(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC)).toString();
        String[] parts = topic.split("/");
        Executor executor = executors.get(parts[2]);
/*        if(executor != null) {
            executor.run(message);
        }*/
        MetricType metricType = MetricUtil.getMetricType(parts[2]);
        var metric = Metric.builder()
                .type(metricType)
                .value(message.getPayload().toString())
                .timestamp(message.getHeaders().getTimestamp())
                .deviceId(parts[1]).build();
        int value = Integer.parseInt(metric.getValue());
        var ranges = List.of(Range.ALL, Range.ADULT);
        Level level = configService.findAll().get(metricType).stream()
                        .filter(config -> ranges.contains(config.getRange()))
                        .filter(config -> value >= config.getMin() && value <= config.getMax())
                        .findFirst().orElseThrow().getLevel();
        announce(metric, level);
//        metricRepository.save(metric);
    }

    private void announce(Metric metric, Level level) {
        switch (level) {
            case NORMAL -> System.out.println("do nothing");
            case WARNING -> System.out.println("send not. by device id");
            case CRITICAL -> System.out.println("send not. via web socket");
            case EMERGENCY -> System.out.println("send emergency, get gps");
        }
    }
}
