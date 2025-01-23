package kg.edu.manas.cloud.exception;

import jakarta.persistence.EntityNotFoundException;
import kg.edu.manas.cloud.model.data.record.ErrorResponseRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.security.auth.login.CredentialExpiredException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseRecord> handleException(ConflictException ex, WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponseRecord(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseRecord> handleException(EntityNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponseRecord(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(CredentialExpiredException.class)
    public ResponseEntity<ErrorResponseRecord> handleException(CredentialExpiredException ex, WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponseRecord(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseRecord> handleException(AuthenticationException ex, WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponseRecord(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseRecord> handleException(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponseRecord(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
