package kg.edu.manas.cloud.service;

import kg.edu.manas.cloud.model.data.record.MetricChartRecord;
import kg.edu.manas.cloud.model.repository.DeviceRepository;
import kg.edu.manas.cloud.model.repository.MetricRepository;
import kg.edu.manas.cloud.util.StatisticsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class DataInterpretationService {
    private final CustomerService customerService;
    private final DeviceRepository deviceRepository;
    private final MetricRepository metricRepository;

    public List<MetricChartRecord> getTimeBuckets(String type, LocalDate targetDay, Optional<String> user) {
        return metricRepository.getTimeBuckets(getDeviceId(user), type, targetDay);
    }

    public Map<String, Object> getStandardDeviation(String type, LocalDate targetDay, Optional<String> user) {
        var sunrise = targetDay.atTime(6, 0);
        var sunset = targetDay.atTime(22, 0);
        var daytimeData = Arrays.stream(metricRepository.getValuesFromTo(getDeviceId(user), type, sunrise, sunset)).mapToDouble(o -> (Double) o).toArray();
        var nighttimeData = Arrays.stream(metricRepository.getValuesFromTo(getDeviceId(user), type, sunset, sunrise)).mapToDouble(o -> (Double) o).toArray();

        return Map.of(
                "daytime", Map.of("mean", StatisticsUtil.findMean(daytimeData), "standardDeviation", StatisticsUtil.findStandardDeviation(daytimeData)),
                "nighttime", Map.of("mean", StatisticsUtil.findMean(nighttimeData), "standardDeviation", StatisticsUtil.findStandardDeviation(nighttimeData))
        );
    }
    public
    Map<String, Object> getRelation(String firstType, String secondType, LocalDate targetDay, Optional<String> user) {
        String deviceId = getDeviceId(user);
        try {
            var xAvgMetrics = CompletableFuture.supplyAsync(() -> metricRepository.getAvgMetrics(deviceId, firstType, targetDay));
            var yAvgMetrics = CompletableFuture.supplyAsync(() -> metricRepository.getAvgMetrics(deviceId, secondType, targetDay));
            double correlation = findCorrelation(deviceId, firstType, secondType, targetDay);
            return Map.of(
                    "firstType", xAvgMetrics.get(),
                    "secondType", yAvgMetrics.get(),
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

    private String getDeviceId(Optional<String> user) {
        String username;

        if(user.isEmpty()) {
            username = customerService.getPrincipal();
        } else {
            username = customerService.authorizeUser(user.get());
        }
        return deviceRepository.getDeviceIdByUsername(username);
    }
}
