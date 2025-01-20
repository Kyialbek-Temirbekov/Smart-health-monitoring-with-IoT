package kg.edu.manas.cloud.data.record;

import org.springframework.http.HttpStatus;

public record ErrorResponseRecord(HttpStatus status, String message) {
}
