package setecolinas.com.sis_task_manager.config;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(ChangeSetPersister.NotFoundException e) {
        return ResponseEntity.status(NOT_FOUND).body(new ApiError(NOT_FOUND.value(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleRequestNotValidException(MethodArgumentNotValidException e) {

        List<String> errors = new ArrayList<>();
        e.getBindingResult()
                .getFieldErrors().forEach(error -> errors.add(error.getField() + ": " + error.getDefaultMessage()));
        e.getBindingResult()
                .getGlobalErrors() //Global errors are not associated with a specific field but are related to the entire object being validated.
                .forEach(error -> errors.add(error.getObjectName() + ": " + error.getDefaultMessage()));

        String message = "Validation of request failed: %s".formatted(String.join(", ", errors));
        return ResponseEntity.status(BAD_REQUEST).body(new ApiError(BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException() {
        return ResponseEntity.status(UNAUTHORIZED)
                .body(new ApiError(UNAUTHORIZED.value(), "Invalid username or password"));
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ApiError> handleDuplicateException(DuplicateException e) {
        return ResponseEntity.status(CONFLICT).body(new ApiError(CONFLICT.value(), e.getMessage()));
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ApiError> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        return ResponseEntity.status(UNAUTHORIZED).body(new ApiError(UNAUTHORIZED.value(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnknownException(Exception e) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiError(INTERNAL_SERVER_ERROR.value(), e.getMessage()));
    }

}
