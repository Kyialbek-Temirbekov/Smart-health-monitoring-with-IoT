package kg.edu.manas.cloud;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import kg.edu.manas.cloud.model.data.enums.MetricType;
import kg.edu.manas.cloud.model.entity.Metric;
import kg.edu.manas.cloud.model.repository.MetricRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@OpenAPIDefinition(
		info = @Info(
				title = "IoT Cloud REST API Documentation",
				description = "<h3>Authentication:</h3><br> <b>JWT</b>: Bearer {jwt_token}<br> <b>Basic</b>: Basic {Base64.encode(username:password)}"
		)
)
public class CloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudApplication.class, args);
	}

//	@Bean
	public CommandLineRunner commandLineRunner(MetricRepository metricRepository) {
		return args -> {
			List<String> dayList = new ArrayList<>();
			List<String> nightList = new ArrayList<>();
			for(String line : daytimeHr100.split("\\R")) {
				dayList.add(line.split(",")[1]);
			}
			for(String line : nighttimeHr100.split("\\R")) {
				nightList.add(line.split(",")[1]);
			}
            var ref = new Object() {
                LocalDateTime timestamp = LocalDateTime.of(2025, 3, 5, 6, 0);
            };
			for(int i=0; i < 7; i++) {
				dayList.forEach(day -> {
					ref.timestamp = ref.timestamp.plusMinutes(1);
					var metric = Metric.builder()
							.type(MetricType.HEART_BEAT)
							.timestamp(ref.timestamp)
							.value(day)
							.deviceId("0quVwI9n6xEPhWUJ5aEzTS9gAoZMBMrmbKC+4on7ROZJ72ivk79M9YTd5/OZckxm")
							.build();
					metricRepository.save(metric);
				});
			}
			for(int i=0; i < 7; i++) {
				nightList.forEach(night -> {
					ref.timestamp = ref.timestamp.plusMinutes(1);
					var metric = Metric.builder()
							.type(MetricType.HEART_BEAT)
							.timestamp(ref.timestamp)
							.value(night)
							.deviceId("0quVwI9n6xEPhWUJ5aEzTS9gAoZMBMrmbKC+4on7ROZJ72ivk79M9YTd5/OZckxm")
							.build();
					metricRepository.save(metric);
				});
			}
		};
	}
	private static final String nighttimeHr100 = """
			1,88
			2,89
			3,92
			4,95
			5,98
			6,102
			7,105
			8,107
			9,106
			10,104
			11,100
			12,94
			13,90
			14,87
			15,85
			16,84
			17,68
			18,65
			19,63
			20,60
			21,58
			22,57
			23,60
			24,64
			25,68
			26,73
			27,80
			28,87
			29,95
			30,108
			31,115
			32,122
			33,125
			34,124
			35,120
			36,115
			37,110
			38,106
			39,101
			40,95
			41,90
			42,87
			43,85
			44,84
			45,82
			46,82
			47,82
			48,110
			49,115
			50,118
			51,117
			52,115
			53,110
			54,104
			55,98
			56,92
			57,87
			58,82
			59,76
			60,71
			61,65
			62,62
			63,60
			64,59
			65,58
			66,60
			67,64
			68,70
			69,77
			70,92
			71,100
			72,110
			73,118
			74,124
			75,126
			76,125
			77,120
			78,114
			79,105
			80,96
			81,90
			82,86
			83,83
			84,80
			85,79
			86,78
			87,80
			88,82
			89,85
			90,90
			91,95
			92,100
			93,98
			94,93
			95,88
			96,84
			97,80
			98,76
			99,73
			100,70
			""";
	private static final String daytimeHr100 = """
			1,72
			2,73
			3,74
			4,75
			5,77
			6,79
			7,81
			8,83
			9,85
			10,87
			11,89
			12,91
			13,92
			14,93
			15,94
			16,95
			17,95
			18,94
			19,93
			20,92
			21,90
			22,88
			23,86
			24,84
			25,82
			26,80
			27,78
			28,76
			29,74
			30,73
			31,72
			32,71
			33,70
			34,70
			35,71
			36,72
			37,73
			38,75
			39,77
			40,79
			41,81
			42,83
			43,85
			44,87
			45,89
			46,90
			47,92
			48,93
			49,94
			50,95
			51,94
			52,93
			53,92
			54,90
			55,88
			56,86
			57,84
			58,82
			59,80
			60,78
			61,76
			62,74
			63,73
			64,72
			65,71
			66,70
			67,70
			68,71
			69,72
			70,74
			71,76
			72,78
			73,80
			74,82
			75,84
			76,86
			77,88
			78,90
			79,91
			80,93
			81,94
			82,95
			83,94
			84,93
			85,92
			86,90
			87,88
			88,86
			89,84
			90,82
			91,80
			92,78
			93,76
			94,74
			95,73
			96,72
			97,71
			98,70
			99,71
			100,72
			""";
}
