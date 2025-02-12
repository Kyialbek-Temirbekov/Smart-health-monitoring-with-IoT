package kg.edu.manas.cloud.service;

import kg.edu.manas.cloud.model.data.record.MetricChartRecord;
import kg.edu.manas.cloud.model.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DataInterpretationService {
    private final AnalyticsService analyticsService;
    private final CustomerService customerService;
    private final DeviceRepository deviceRepository;

    public List<MetricChartRecord> getTimeBuckets(String type, LocalDate targetDay, Optional<String> user) {
        return analyticsService.getTimeBuckets(getDeviceId(user), type, targetDay);
    }
    public Map<String, Double> getStandardDeviation(String type, LocalDate targetDay, Optional<String> user) {
        return analyticsService.getStandardDeviation(getDeviceId(user), type, targetDay);
    }
    public Map<String, Object> getRelation(String firstType, String secondType, LocalDate targetDay, Optional<String> user) {
        return analyticsService.getRelation(getDeviceId(user), firstType, secondType, targetDay);
    }
    private String getDeviceId(Optional<String> user) {
        String username = user.map(customerService::authorizeUser).orElse(customerService.getPrincipal());
        return deviceRepository.getDeviceIdByUsername(username);
    }

}
