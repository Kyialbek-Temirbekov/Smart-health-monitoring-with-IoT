package kg.edu.manas.cloud.service;

import kg.edu.manas.cloud.date.enums.Level;
import kg.edu.manas.cloud.date.enums.Range;
import kg.edu.manas.cloud.entity.Metric;
import kg.edu.manas.cloud.executor.Executor;
import kg.edu.manas.cloud.repository.MetricRepository;
import kg.edu.manas.cloud.util.MetricUtil;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    public void accept(Message<?> message) {
        System.out.println("Message received");
        var topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();
        var executor = executors.get(topic.substring(topic.lastIndexOf('/') + 1));
        if(executor != null) {
            executor.run(message);
        }

        var headers = message.getHeaders();
        var parts = topic.split("/");
        var metricType = MetricUtil.getMetricType(parts[2]);
        var config = configService.findAll();

        var metric = Metric.builder()
                .type(metricType)
                .value(message.getPayload().toString())
                .timestamp(headers.getTimestamp())
                .deviceId(parts[1])
                .build();

        int value = Integer.parseInt(metric.getValue());
        var range = Range.ALL;
        var level = config.get(metricType).stream()
                        .filter(state -> range.equals(state.getRange()))
                        .filter(state -> value >= state.getMin() && value <= state.getMax())
                        .findFirst().get().getLevel();
        process(metric, level);

//        metricRepository.save(metric);
    }

    private void process(Metric metric, Level level) {
        switch (level) {
            case NORMAL -> System.out.println("do nothing");
            case WARNING -> System.out.println("send not. by device id");
            case CRITICAL -> System.out.println("send not. via web socket");
            case EMERGENCY -> System.out.println("send emergency, get gps");
        }
    }
}
