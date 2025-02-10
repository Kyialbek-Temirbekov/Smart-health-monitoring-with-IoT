package kg.edu.manas.cloud;

import kg.edu.manas.cloud.service.AnalyticsService;
import kg.edu.manas.cloud.util.StatisticsUtil;
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
	public CommandLineRunner commandLineRunner(AnalyticsService service) {
		return args -> {
			double[] arr = new double[] { 73, 73, 77, 78, 79, 99, 98, 99, 100, 102, 102, 100};
			double[] aar = new double[] { 0, 0, 2, 3, 3, 21, 21, 22, 24, 25, 26, 26};
			var mean = StatisticsUtil.findMean(arr);
			var sd = StatisticsUtil.findStandardDeviation(arr);
			System.out.println("Mean: " + mean);
			System.out.println("SD: " + sd + " : " + (sd / mean) * 100);
			System.out.println("Correlation: " + StatisticsUtil.findPearsonsCorrelation(arr, aar));
		};
	}
}
