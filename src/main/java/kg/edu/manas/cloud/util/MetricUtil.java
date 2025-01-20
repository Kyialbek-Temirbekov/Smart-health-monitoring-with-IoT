package kg.edu.manas.cloud.util;

import kg.edu.manas.cloud.data.enums.MetricType;

public class MetricUtil {
    public static MetricType getMetricType(String metricName) {
        return switch (metricName) {
            case "heart-beat" -> MetricType.HEART_BEAT;
            case "saturation" -> MetricType.SATURATION;
            case "air-quality" -> MetricType.AIR_QUALITY;
            case "step-count" -> MetricType.STEP_COUNT;
            case "calorie-burn" -> MetricType.CALORIE_BURN;
            case "gps" -> MetricType.GPS;
            default -> null;
        };
    }
}
