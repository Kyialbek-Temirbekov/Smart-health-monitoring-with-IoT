package kg.edu.manas.cloud.repository;

import kg.edu.manas.cloud.data.enums.MetricType;
import kg.edu.manas.cloud.entity.Metric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetricRepository extends JpaRepository<Metric, Long> {
    Metric findLastMetricByDeviceIdAndType(String deviceId, MetricType type);
}
