package kg.edu.manas.cloud.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.edu.manas.cloud.model.data.record.MetricChartRecord;
import kg.edu.manas.cloud.service.DataInterpretationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Data Interpretation Service")
@RestController
@RequestMapping("/data/interpretation")
@RequiredArgsConstructor
public class DataInterpretationController {
    private final DataInterpretationService dataInterpretationService;

    @Operation(summary = "get avg, min, max values for chart")
    @GetMapping("/chart")
    public List<MetricChartRecord> getTimeBuckets(
            @RequestParam String type,
            @RequestParam LocalDate targetDay,
            @RequestParam(required = false) String user
    ) {
        return dataInterpretationService.getTimeBuckets(type, targetDay, Optional.ofNullable(user));
    }
    @Operation(summary = "get standard deviation and mean")
    @GetMapping("/standard-deviation")
    public Map<String, Object> getStandardDeviation(
            @RequestParam String type,
            @RequestParam LocalDate targetDay,
            @RequestParam(required = false) String user
    ) {
        return dataInterpretationService.getStandardDeviation(type, targetDay, Optional.ofNullable(user));
    }
    @Operation(summary = "get avg values for chart and correlation")
    @GetMapping("/relation")
    public Map<String, Object> getRelation(
            @RequestParam String firstType,
            @RequestParam String secondType,
            @RequestParam LocalDate targetDay,
            @RequestParam(required = false) String user
    ) {
        return dataInterpretationService.getRelation(firstType, secondType, targetDay, Optional.ofNullable(user));
    }
}
