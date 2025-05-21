package kg.edu.manas.cloud.model.data.record;

import java.time.LocalDate;

public record CreateDeviceRecord(
        String id,
        String brand,
        String model,
        LocalDate releaseDate,
        String batteryLife
) {
}
