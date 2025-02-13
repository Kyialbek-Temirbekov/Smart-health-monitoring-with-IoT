package kg.edu.manas.cloud.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.edu.manas.cloud.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Configuration Service")
@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class ConfigController {
    private final ConfigService configService;

    @Operation(summary = "clear cache")
    @GetMapping("/clear-cache")
    public String clearCache() {
        return configService.clearCache();
    }
}
