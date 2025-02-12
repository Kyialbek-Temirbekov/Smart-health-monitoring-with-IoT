package kg.edu.manas.cloud.model.data.record;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MetricChartRecord(Instant epoch, Float average, Float maxValue, Float minValue) {
}
