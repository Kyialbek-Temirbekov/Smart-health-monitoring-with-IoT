package kg.edu.manas.cloud.entity;

import jakarta.persistence.*;
import kg.edu.manas.cloud.date.enums.MetricType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
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
    private Long timestamp;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", referencedColumnName = "id")
    private Device device;
}
