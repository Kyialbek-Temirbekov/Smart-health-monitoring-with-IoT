package kg.edu.manas.cloud.model.repository;

import kg.edu.manas.cloud.model.data.enums.MetricType;
import kg.edu.manas.cloud.model.data.record.MetricChartRecord;
import kg.edu.manas.cloud.model.entity.Metric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MetricRepository extends JpaRepository<Metric, Long> {
    Optional<Metric> findFirstByDeviceIdAndTypeOrderByTimestampDesc(String deviceId, MetricType type);
    @Query(name = "MetricChartQuery", nativeQuery = true)
    List<MetricChartRecord> getTimeBuckets(String deviceId, String type, LocalDate targetDay);
    @Query(name = "MetricAvgChartQuery", nativeQuery = true)
    List<MetricChartRecord> getAvgMetrics(String deviceId, String type, LocalDate targetDay);
    @Query(value = """
        select cast(value as double precision)
        from metric
        where device_id = :deviceId and type = :type
        and timestamp >= :targetDay
        and timestamp < cast(:targetDay as date) + interval '1 day'
    """, nativeQuery = true)
    Object[] getValues(String deviceId, String type, LocalDate targetDay);
}
