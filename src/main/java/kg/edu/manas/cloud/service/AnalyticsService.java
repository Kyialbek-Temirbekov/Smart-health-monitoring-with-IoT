package kg.edu.manas.cloud.service;

import kg.edu.manas.cloud.model.repository.MetricRepository;
import kg.edu.manas.cloud.util.StatisticsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final MetricRepository metricRepository;

    public Object[] findStandardDeviation(String deviceId, String type, LocalDate targetDay) {
        var data = Arrays.stream(metricRepository.getValues(deviceId, type, targetDay)).mapToDouble(o -> (Double) o).toArray();
        var mean = StatisticsUtil.findMean(data);
        var stdDev = StatisticsUtil.findStandardDeviation(data);
        return new Object[] {mean, stdDev};
    }
    public double findCorrelation(String deviceId, String xType, String yType, LocalDate targetDay) throws ExecutionException, InterruptedException {
        return StatisticsUtil.findPearsonsCorrelation(
                CompletableFuture.supplyAsync(
                        () -> Arrays.stream(metricRepository.getValues(deviceId, xType, targetDay)).mapToDouble(o -> (Double) o).toArray()
                ).get(),
                CompletableFuture.supplyAsync(
                        () -> Arrays.stream(metricRepository.getValues(deviceId, yType, targetDay)).mapToDouble(o -> (Double) o).toArray()
                ).get()
        );
    }
}
