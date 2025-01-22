package kg.edu.manas.cloud.service.executor;

import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class HeartBeatExecutor implements Executor {
    @Override
    public void run(Message<?> message) {
        System.out.println("HeartBeatExecutor.run() called " + message.getPayload());
        System.out.println(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC));
        System.out.println();
    }

    @Override
    public String getName() {
        return "heart-beat";
    }
}
