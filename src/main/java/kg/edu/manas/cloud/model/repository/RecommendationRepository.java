package kg.edu.manas.cloud.model.repository;

import kg.edu.manas.cloud.model.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    <T> List<T> findAllByDeviceId(String deviceId, Class<T> type);
    boolean existsByDeviceIdAndTimestampIsAfter(String deviceId, LocalDateTime timestamp);
}
