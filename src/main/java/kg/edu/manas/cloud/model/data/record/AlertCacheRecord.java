package kg.edu.manas.cloud.model.data.record;

import kg.edu.manas.cloud.model.data.enums.Level;
import kg.edu.manas.cloud.model.data.enums.MetricType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class AlertCacheRecord implements Serializable {
    private Level level;
    private long timestamp;
    private int count;
    private boolean sent;
}
