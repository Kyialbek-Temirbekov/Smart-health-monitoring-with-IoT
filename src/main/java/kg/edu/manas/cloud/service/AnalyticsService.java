package kg.edu.manas.cloud.service;

import kg.edu.manas.cloud.model.data.record.MetricChartRecord;
import kg.edu.manas.cloud.model.repository.MetricRepository;
import kg.edu.manas.cloud.util.StatisticsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final MetricRepository metricRepository;

    public List<MetricChartRecord> getTimeBuckets(String deviceId, String type, LocalDate targetDay) {
        return metricRepository.getTimeBuckets(deviceId, type, targetDay);
    }
    public Map<String, Double> getStandardDeviation(String deviceId, String type, LocalDate targetDay) {
        var data = Arrays.stream(metricRepository.getValues(deviceId, type, targetDay)).mapToDouble(o -> (Double) o).toArray();
        var mean = StatisticsUtil.findMean(data);
        var stdDev = StatisticsUtil.findStandardDeviation(data);
        return Map.of("mean", mean, "standardDeviation", stdDev);
    }
    public Map<String, Object> getRelation(String deviceId, String firstType, String secondType, LocalDate targetDay) {
        try {
            var xAvgMetrics = CompletableFuture.supplyAsync(() -> metricRepository.getAvgMetrics(deviceId, firstType, targetDay));
            var yAvgMetrics = CompletableFuture.supplyAsync(() -> metricRepository.getAvgMetrics(deviceId, secondType, targetDay));
            double correlation = findCorrelation(deviceId, firstType, secondType, targetDay);
            return Map.of(
                    "firstType:" + firstType, xAvgMetrics.get(),
                    "secondType:" + secondType, yAvgMetrics.get(),
                    "correlation", correlation
            );
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private double findCorrelation(String deviceId, String firstType, String secondType, LocalDate targetDay) throws ExecutionException, InterruptedException {
        var yValues = CompletableFuture.supplyAsync(
                () -> Arrays.stream(metricRepository.getValues(deviceId, secondType, targetDay)).mapToDouble(o -> (Double) o).toArray()
        );
        return StatisticsUtil.findPearsonsCorrelation(
                Arrays.stream(metricRepository.getValues(deviceId, firstType, targetDay)).mapToDouble(o -> (Double) o).toArray(),
                yValues.get()
        );
    }
}
