package kg.edu.manas.cloud.model.data.record;

import java.time.Instant;

public record MetricAvgRecord(Instant epoch, Float average) {
}
