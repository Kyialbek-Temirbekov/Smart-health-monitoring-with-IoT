package kg.edu.manas.cloud;

import kg.edu.manas.cloud.model.entity.Device;
import kg.edu.manas.cloud.model.repository.CustomerRepository;
import kg.edu.manas.cloud.model.repository.DeviceRepository;
import kg.edu.manas.cloud.service.EncryptionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@EnableCaching
public class CloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(CustomerRepository customerRepository, DeviceRepository deviceRepository, EncryptionService encryptionService) {
		return args -> {

		};
	}
}
