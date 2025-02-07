package kg.edu.manas.cloud.model.entity;

import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode
public class MetricKey implements Serializable {
    private Long id;
    private LocalDateTime timestamp;
}
