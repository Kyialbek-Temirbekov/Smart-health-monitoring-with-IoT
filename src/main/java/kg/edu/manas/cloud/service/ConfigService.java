package kg.edu.manas.cloud.service;

import kg.edu.manas.cloud.model.data.constants.Messages;
import kg.edu.manas.cloud.model.data.enums.MetricType;
import kg.edu.manas.cloud.model.entity.Config;
import kg.edu.manas.cloud.model.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfigService {
    private final ConfigRepository configRepository;

    @Cacheable(value = "config")
    public Map<MetricType, List<Config>> findAll() {
        return configRepository.findAll()
                .stream().collect(Collectors.groupingBy(Config::getName));
    }

    @CacheEvict(value = "config", allEntries = true)
    public String clearCache() {
        return Messages.CACHE_CLEARED;
    }
}
