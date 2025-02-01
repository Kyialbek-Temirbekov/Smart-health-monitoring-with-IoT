package kg.edu.manas.cloud.util;

import kg.edu.manas.cloud.model.data.constants.Messages;
import kg.edu.manas.cloud.model.data.enums.Level;
import kg.edu.manas.cloud.model.data.enums.MetricType;
import kg.edu.manas.cloud.model.data.enums.Range;

import static kg.edu.manas.cloud.model.data.enums.Range.*;

public class MetricUtil {
    public static String getMetricName(MetricType metricType) {
        return switch (metricType) {
            case HEART_BEAT -> Messages.HEART_BEAT;
            case SATURATION -> Messages.SATURATION;
            case AIR_QUALITY -> Messages.AIR_QUALITY;
            default -> "_";
        };
    }

    public static MetricType getMetricType(String metricName) {
        return switch (metricName) {
            case "heart-beat" -> MetricType.HEART_BEAT;
            case "saturation" -> MetricType.SATURATION;
            case "air-quality" -> MetricType.AIR_QUALITY;
            case "step-count" -> MetricType.STEP_COUNT;
            case "calorie-burn" -> MetricType.CALORIE_BURN;
            case "gps" -> MetricType.GPS;
            default -> MetricType.UNKNOWN;
        };
    }
    public static Range getRange(int age) {
        if(age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        } else if(age < 18) {
            return CHILDREN;
        } else if(age < 60) {
            return ADULT;
        } else {
            return ELDERLY;
        }
    }
    public static boolean isPriorityHigher(Level subject, Level object) {
        return subject.ordinal() > object.ordinal();
    }
    public static boolean isPriorityEqual(Level subject, Level object) {
        return subject.ordinal() == object.ordinal();
    }
}
