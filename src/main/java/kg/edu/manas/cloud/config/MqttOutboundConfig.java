package kg.edu.manas.cloud.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;

@Configuration
public class MqttOutboundConfig {
    @Bean
    public MqttPahoClientFactory mqttOutboundClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{"tcp://broker.hivemq.com:1883"});
        options.setKeepAliveInterval(60);
//        options.setConnectionTimeout(10);
//        options.setUserName("SGBkRMkhhYlRlrMCx9");
//        options.setPassword("G8gOOvKfJhFKK2Zsf4".toCharArray());
        options.setAutomaticReconnect(true);
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MqttPahoMessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("MqttOutbound", mqttOutboundClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("mytopic");
        messageHandler.setDefaultQos(1);
        messageHandler.setTopicExpressionString("headers['mqtt_topic']");
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MqttGateway {
        void sendToMqtt(String data, @Header("mqtt_topic") String topic);
    }
}
