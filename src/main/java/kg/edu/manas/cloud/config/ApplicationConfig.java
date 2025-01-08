package kg.edu.manas.cloud.config;

import kg.edu.manas.cloud.executor.Executor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class ApplicationConfig {
    private final ApplicationContext applicationContext;

    public ApplicationConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public Map<String, Executor> executors() {
        return applicationContext.getBeansOfType(Executor.class).values().stream()
                .collect(Collectors.toMap(Executor::getName, executor -> executor));
    }
}
