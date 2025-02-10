package kg.edu.manas.cloud.model.entity;

import jakarta.persistence.*;
import kg.edu.manas.cloud.model.data.enums.MetricType;
import kg.edu.manas.cloud.model.data.record.MetricChartRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@IdClass(MetricKey.class)
@SqlResultSetMapping(
        name = "MetricChartRecordMapping",
        classes = @ConstructorResult(
                targetClass = MetricChartRecord.class,
                columns = {
                        @ColumnResult(name = "epoch", type = Instant.class),
                        @ColumnResult(name = "average", type = Float.class),
                        @ColumnResult(name = "maxValue", type = Float.class),
                        @ColumnResult(name = "minValue", type = Float.class)
                }
        )
)
@NamedNativeQuery(name = "MetricChartQuery",
        query = """
        select time_bucket('2 min', timestamp) as epoch,
               avg(cast(value as float)) as average,
               max(cast(value as float)) as maxValue,
               min(cast(value as float)) as minValue
        from metric
        where device_id = :deviceId and type = :type
        and timestamp >= :targetDay
        and timestamp < cast(:targetDay as date) + interval '1 day'
        group by epoch
        order by epoch
    """, resultSetMapping = "MetricChartRecordMapping"
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Metric {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "metric_id_gen")
    @SequenceGenerator(name = "metric_id_gen", sequenceName = "metric_seq", allocationSize = 1)
    private Long id;
    @Enumerated(EnumType.STRING)
    private MetricType type;
    private String value;
    @Id
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Device device;
    @Column(name = "device_id")
    private String deviceId;
}
