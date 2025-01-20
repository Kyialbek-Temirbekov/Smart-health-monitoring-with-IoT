package kg.edu.manas.cloud.api;

import kg.edu.manas.cloud.entity.Config;
import kg.edu.manas.cloud.service.ConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ConfigApi {
    private final ConfigService configService;

    public ConfigApi(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping("/config")
    public List<Config> findAll() {
        return configService.findAll();
    }
    @GetMapping("/clear-cache")
    public String clearCache() {
        configService.clearCache();
        return "Cache cleared successfully";
    }
}
