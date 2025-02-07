package kg.edu.manas.cloud.model.data.record;

import java.time.Instant;

public record MetricChartRecord(Instant epoch, Float average, Float maxValue, Float minValue) {
}
