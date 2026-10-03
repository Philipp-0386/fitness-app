package de.phil.fitness.backend.common;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.exc.InvalidFormatException;
/**
 * Handles exceptions globally.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * Handles every exception of this application. Status, code and client message come from the exception itself.
     *
     * <p>Server errors are logged as errors, everything else as a warning.
     * @param ex The exception object thrown
     * @return Returns a {@link ResponseEntity} containing key information regarding the exception
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, HttpServletRequest req) {
        if (ex.getStatus().is5xxServerError()) {
            log.error("{} path={} {}", ex.getCode(), req.getRequestURI(), ex.getMessage());
        } else {
            log.warn("{} path={} {}", ex.getCode(), req.getRequestURI(), ex.getMessage());
        }
        return ResponseEntity
                .status(ex.getStatus())
                .body(new ErrorResponse(
                        ex.getCode(),
                        ex.getClientMessage(),
                        req.getRequestURI()
                ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
        log.warn("Login failed: invalid credentials. path={}", req.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        "INVALID_CREDENTIALS",
                        "Username or password is incorrect",
                        req.getRequestURI()
                ));
    }

    /**
     * Handles request bodies that cannot be parsed, e.g. malformed or truncated JSON.
     *
     * <p>Without this the request falls through to Spring's error dispatch, which answers in a
     * different shape than the rest of the API.
     *
     * <p>A value outside an enum fails here rather than in bean validation, because deserialization
     * runs first. It is reported as a field error so that the client sees the same shape it gets for
     * every other rejected field.
     * @param ex The exception object thrown
     * @return Returns a {@link ResponseEntity} containing key information regarding the exception
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex, HttpServletRequest req) {
        if (ex.getCause() instanceof InvalidFormatException cause && cause.getTargetType().isEnum()) {
            String field = cause.getPath().isEmpty()
                    ? "unknown"
                    : cause.getPath().get(cause.getPath().size() - 1).getPropertyName();
            String allowed = Arrays.stream(cause.getTargetType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            log.warn("Rejected unknown enum value. path={} field={}", req.getRequestURI(), field);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(
                            "VALIDATION_FAILED",
                            "Request body contains invalid fields",
                            req.getRequestURI(),
                            Map.of(field, "must be one of: " + allowed)
                    ));
        }
        log.warn("Rejected unreadable request body. path={} {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "INVALID_JSON",
                        "Request body contains invalid JSON",
                        req.getRequestURI()
                ));
    }

    /**
     * Handles request bodies that violate bean validation constraints.
     * @param ex The exception object thrown
     * @return Returns a {@link ResponseEntity} containing key information regarding the exception,
     *         with one message per rejected field
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationFailed(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            // A field can violate several constraints; reporting the first one is enough for the client.
            fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        log.warn("Rejected invalid request body. path={} fields={}", req.getRequestURI(), fieldErrors.keySet());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "VALIDATION_FAILED",
                        "Request body contains invalid fields",
                        req.getRequestURI(),
                        fieldErrors
                ));
    }

    /**
     * Handles requests to paths that no handler is mapped to.
     *
     * <p>Replaces Spring's default error dispatch body so that a 404 carries the same shape as
     * every other error of this API.
     * @param ex The exception object thrown
     * @return Returns a {@link ResponseEntity} containing key information regarding the exception
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest req) {
        log.warn("No handler mapped. path={}", req.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        "RESOURCE_NOT_FOUND",
                        "No resource exists at this path",
                        req.getRequestURI()
                ));
    }
}
