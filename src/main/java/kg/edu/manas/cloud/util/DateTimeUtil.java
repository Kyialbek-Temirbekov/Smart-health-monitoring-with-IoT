package kg.edu.manas.cloud.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    public static String format(LocalDateTime timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        return timestamp.format(formatter);
    }
}
