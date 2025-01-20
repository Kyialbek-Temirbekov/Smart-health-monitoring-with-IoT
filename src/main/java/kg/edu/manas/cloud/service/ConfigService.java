package kg.edu.manas.cloud.service;

import kg.edu.manas.cloud.entity.Config;
import kg.edu.manas.cloud.repository.ConfigRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigService {
    private final ConfigRepository configRepository;

    public ConfigService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Cacheable(value = "config")
    public List<Config> findAll() {
        return configRepository.findAll();
    }

    @CacheEvict(value = "config", allEntries = true)
    public void clearCache() {
    }
}
