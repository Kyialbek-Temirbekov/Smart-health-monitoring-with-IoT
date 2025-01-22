package kg.edu.manas.cloud.model.data.record;

import org.springframework.http.HttpStatus;

public record ErrorResponseRecord(HttpStatus status, String message) {
}
