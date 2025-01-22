package kg.edu.manas.cloud.service.executor;

import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class AirQualityExecutor implements Executor {
    @Override
    public void run(Message<?> message) {
        System.out.println("Air quality executor run " + message.getPayload());
        System.out.println(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC));
        System.out.println();
    }

    @Override
    public String getName() {
        return "air-quality";
    }
}
