package kg.edu.manas.cloud.model.data.enums;

import lombok.Getter;

@Getter
public enum Level {
    NORMAL(1),
    WARNING(2),
    CRITICAL(3),
    EMERGENCY(4);

    private final int priority;

    Level(int priority) {
        this.priority = priority;
    }
}
