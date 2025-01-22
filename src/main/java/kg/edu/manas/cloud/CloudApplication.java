package kg.edu.manas.cloud;

import kg.edu.manas.cloud.model.cache.RedisCache;
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
	public CommandLineRunner commandLineRunner(RedisCache cache) {
		return args -> {
			System.out.println("Hello Redis");
			cache.putWithTTL("key1", "BARAKADABRA");
			cache.putWithTTL("key1", "barakadabra");
			System.out.println(cache.get("key1"));
			Thread.sleep(4000);
			System.out.println(cache.get("key1"));
		};
	}
}
