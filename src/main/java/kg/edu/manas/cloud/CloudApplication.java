package kg.edu.manas.cloud;

import kg.edu.manas.cloud.config.MqttOutboundConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@SpringBootApplication
public class CloudApplication {
	private final MqttOutboundConfig.MqttGateway mqttGateway;

	public static void main(String[] args) {
		SpringApplication.run(CloudApplication.class, args);
	}

	@GetMapping("/msg")
	public void msg(@RequestParam String value) {
		mqttGateway.sendToMqtt(value, "device/4381/msg");
	}
}
