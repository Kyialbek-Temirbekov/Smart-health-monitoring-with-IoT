package kg.edu.manas.cloud.api;

import kg.edu.manas.cloud.service.ConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
public class ConfigController {
    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }
    @GetMapping("/clear-cache")
    public String clearCache() {
        configService.clearCache();
        return "Cache cleared successfully";
    }
}
