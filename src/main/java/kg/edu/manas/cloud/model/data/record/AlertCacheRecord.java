package kg.edu.manas.cloud.model.data.record;

import kg.edu.manas.cloud.model.data.enums.Level;
import kg.edu.manas.cloud.model.data.enums.MetricType;

import java.io.Serializable;

public record AlertCacheRecord(MetricType metric, Level level) implements Serializable {
}
