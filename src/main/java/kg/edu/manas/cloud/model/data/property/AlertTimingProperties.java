package kg.edu.manas.cloud.model.data.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "application.alert")
@Configuration
@Getter
@Setter
public class AlertTimingProperties {
    private int ttl;
    private int waiting;
    private int block;
    private int eBlock;
    private int total;
    private float threshold;
}
