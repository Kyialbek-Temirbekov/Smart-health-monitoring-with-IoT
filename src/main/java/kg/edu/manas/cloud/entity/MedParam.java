package kg.edu.manas.cloud.entity;

import jakarta.persistence.*;
import kg.edu.manas.cloud.date.enums.Level;
import kg.edu.manas.cloud.date.enums.MetricType;
import kg.edu.manas.cloud.date.enums.Range;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedParam {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "med_param_id_gen")
    @SequenceGenerator(name = "med_param_id_gen", sequenceName = "med_param_seq", allocationSize = 1)
    private Long id;
    @Enumerated(EnumType.STRING)
    private MetricType name;
    @Enumerated(EnumType.STRING)
    private Level level;
    @Enumerated(EnumType.STRING)
    private Range range;
    private int min;
    private int max;
}
