package kg.edu.manas.cloud.model.entity;

import jakarta.persistence.*;
import kg.edu.manas.cloud.model.data.enums.MetricType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@IdClass(MetricKey.class)
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
    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime timestamp;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Device device;
    @Column(name = "device_id")
    private String deviceId;
}
