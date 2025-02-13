package kg.edu.manas.cloud.service;

import kg.edu.manas.cloud.model.entity.Recommendation;
import kg.edu.manas.cloud.model.repository.DeviceRepository;
import kg.edu.manas.cloud.model.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final DeviceRepository deviceRepository;
    private final CustomerService customerService;

    public List<Recommendation> getRecommendations() {
        String deviceId = deviceRepository.getDeviceIdByUsername(customerService.getPrincipal());
        return recommendationRepository.findAllByDeviceId(deviceId);
    }
}
