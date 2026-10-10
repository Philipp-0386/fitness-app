package de.phil.fitness.backend.common;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
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
                        req.getRequestURI(),
                        ex.getFieldErrors()
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
     * Handles Spring Security authentication failures raised inside a controller or service.
     *
     * Filter chain failures would usually not reach this, and turn into 500s.
     * @param ex The exception object thrown
     * @return Returns a {@link ResponseEntity} containing key information regarding the exception
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex, HttpServletRequest req) {
        log.warn("Rejected unauthenticated request. path={} {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        "UNAUTHENTICATED",
                        "Authentication is required to access this resource",
                        req.getRequestURI()
                ));
    }

    /**
     * Handles Spring Security's access denied raised inside a controller or service, e.g. by method security.
     *
     * Without this handler the catch-all would turn it into a 500.
     * @param ex The exception object thrown
     * @return Returns a {@link ResponseEntity} containing key information regarding the exception
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleSpringAccessDenied(org.springframework.security.access.AccessDeniedException ex,
                                                                  HttpServletRequest req) {
        log.warn("Access denied. path={} {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                        "ACCESS_DENIED",
                        "You do not have access to this resource",
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

    /**
     * Handles a path variable or query parameter that cannot be converted to its type, e.g. a non numeric id.
     * @param ex The exception object thrown
     * @return Returns a {@link ResponseEntity} containing key information regarding the exception
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        log.warn("Rejected parameter of wrong type. path={} parameter={}", req.getRequestURI(), ex.getName());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "INVALID_PARAMETER",
                        "Parameter '" + ex.getName() + "' has an invalid value",
                        req.getRequestURI()
                ));
    }

    /**
     * Handles a write the database rejected, e.g. a unique constraint the service did not check first.
     *
     * Currently only a safety-net that should not be needed, since business logic should create own exception according to the conflict.
     * @param ex The exception object thrown
     * @return Returns a {@link ResponseEntity} containing key information regarding the exception
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest req) {
        String constraint = ex.getCause() instanceof ConstraintViolationException cause
                ? cause.getConstraintName()
                : "unknown";
        log.warn("Database rejected write. path={} constraint={}", req.getRequestURI(), constraint);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        "DATA_CONFLICT",
                        "The request conflicts with existing data",
                        req.getRequestURI()
                ));
    }

    /**
     * Handles everything no other handler covers.
     *
     * Spring's own web exceptions (405, 415, ...) know their status, they keep it and get a code derived from
     * it. Anything else is unexpected and answers 500 without internal details, the stack trace goes to the log.
     * @param ex The exception object thrown
     * @return Returns a {@link ResponseEntity} containing key information regarding the exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest req) {
        if (ex instanceof org.springframework.web.ErrorResponse springError) {
            HttpStatusCode status = springError.getStatusCode();
            HttpStatus known = HttpStatus.resolve(status.value());
            log.warn("Rejected request. path={} status={} {}", req.getRequestURI(), status.value(), ex.getMessage());
            return ResponseEntity
                    .status(status)
                    .body(new ErrorResponse(
                            known != null ? known.name() : "REQUEST_FAILED",
                            known != null ? known.getReasonPhrase() : "The request could not be processed",
                            req.getRequestURI()
                    ));
        }
        log.error("Unexpected exception. path={}", req.getRequestURI(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "INTERNAL_ERROR",
                        "An unexpected error occurred",
                        req.getRequestURI()
                ));
    }
}
