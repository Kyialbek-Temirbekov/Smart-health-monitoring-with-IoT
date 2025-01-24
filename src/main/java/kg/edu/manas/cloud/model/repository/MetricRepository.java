package kg.edu.manas.cloud.model.repository;

import kg.edu.manas.cloud.model.data.enums.MetricType;
import kg.edu.manas.cloud.model.entity.Metric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetricRepository extends JpaRepository<Metric, Long> {
    Optional<Metric> findFirstByDeviceIdAndTypeOrderByTimestampDesc(String deviceId, MetricType type);
}
