package kg.edu.manas.cloud.model.data.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "application.encryption")
@Configuration
@Getter
@Setter
public class EncryptionProperties {
    public String secretKey;
    public String algorithm;
    public String cipherMode;
}
