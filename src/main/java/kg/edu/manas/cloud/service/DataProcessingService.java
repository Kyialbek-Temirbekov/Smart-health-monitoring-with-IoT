package kg.edu.manas.cloud.service;

import kg.edu.manas.cloud.executor.Executor;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DataProcessingService {
    private final Map<String, Executor> executors;

    public DataProcessingService(Map<String, Executor> executors) {
        this.executors = executors;
    }

    public void accept(Message<?> message) {
        var topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
        if(topic != null) {
            var executor = executors.get(topic.toString().substring(topic.toString().lastIndexOf('/') + 1));
            if(executor != null) {
                executor.run(message);
            }
        }
        // save $message in db
    }
}
