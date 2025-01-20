package kg.edu.manas.cloud.service;

import kg.edu.manas.cloud.date.enums.MetricType;
import kg.edu.manas.cloud.entity.Config;
import kg.edu.manas.cloud.repository.ConfigRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConfigService {
    private final ConfigRepository configRepository;

    public ConfigService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Cacheable(value = "config")
    public Map<MetricType, List<Config>> findAll() {
        return configRepository.findAll()
                .stream().collect(Collectors.groupingBy(Config::getName));
    }

    @CacheEvict(value = "config", allEntries = true)
    public void clearCache() {
    }
}
