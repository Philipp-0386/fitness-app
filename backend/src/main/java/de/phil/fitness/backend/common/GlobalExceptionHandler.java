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

import de.phil.fitness.backend.user.exception.InvalidPasswordException;
import de.phil.fitness.backend.auth.exception.AccessDeniedException;
import de.phil.fitness.backend.exercise.exception.ExerciseNotFoundException;
import de.phil.fitness.backend.signup.exception.EmailAlreadyExistsException;
import de.phil.fitness.backend.signup.exception.UsernameAlreadyTaken;
import de.phil.fitness.backend.user.exception.DefaultRoleNotFoundException;
import de.phil.fitness.backend.user.exception.UserNotFoundException;
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
     * Handles case of already existing emails during the sign-up process and creation of a new user.
     * @param ex Accepts the exception object
     * @return  Returns a {@link ResponseEntity} containing key information regarding the exception
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex, HttpServletRequest req) {
        log.warn("User creation rejected. Email already registered. {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        "EMAIL_ALREADY_EXISTS",
                        ex.getMessage(),
                        req.getRequestURI()
                ));
    }

    /**
     * Handles the case of missing default role during user creation process.
     * @param ex Accepts the exception object
     * @return  Returns a {@link ResponseEntity} containing key information regarding the exception
     */
    @ExceptionHandler(DefaultRoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDefaultRoleNotFound(DefaultRoleNotFoundException ex, HttpServletRequest req) {
        log.error("User creation rejected. Default role entry unavailable. {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "DEFAULT_ROLE_NOT_FOUND",
                        ex.getMessage(),
                        req.getRequestURI()
                ));
    }

    /**
     * Handles the case of user's username already being in use.
     * @param ex The exception object thrown
     * @return Returns a {@link ResponseEntity} containing key information regarding the exception
     */
    @ExceptionHandler(UsernameAlreadyTaken.class)
    public ResponseEntity<ErrorResponse> handleUsernameTaken(UsernameAlreadyTaken ex, HttpServletRequest req) {
        log.warn("User creation rejected. Username already taken. {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        "USERNAME_ALREADY_TAKEN",
                        ex.getMessage(),
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
     * Handles the password confirming an account deletion not matching the stored one.
     *
     * Deliberately not 401: the caller is authenticated, only the confirmation failed, so the
     * client must not treat the session as expired.
     * @param ex The exception object thrown
     * @return Returns a {@link ResponseEntity} containing key information regarding the exception
     */
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPassword(InvalidPasswordException ex, HttpServletRequest req) {
        log.warn("Account deletion rejected: wrong password. path={}", req.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                        "INVALID_PASSWORD",
                        "The password is incorrect",
                        req.getRequestURI()
                ));
    }

    /**
     * Handles a valid access token whose user no longer exists, e.g. after the account was deleted
     * while the token had not expired yet.
     *
     * Answers like a missing token, because the session is no longer usable.
     * @param ex The exception object thrown
     * @return Returns a {@link ResponseEntity} containing key information regarding the exception
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, HttpServletRequest req) {
        log.warn("Token subject has no user. path={} {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                        "UNAUTHENTICATED",
                        "Authentication is required to access this resource",
                        req.getRequestURI()
                ));
    }

    /**
     * Handles the case of an authenticated user requesting a resource owned by someone else.
     * @param ex The exception object thrown
     * @return Returns a {@link ResponseEntity} containing key information regarding the exception
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
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
     * Handles the case of an exercise not being available to the requesting user. Either because under the given exerciseId
     * there simply is no exercise listed, because it is soft deleted, or because the requesting user is not the owner,
     * and therefore not allowed to access it.
     *
     * <p>All of those causes deliberately share this response: a 403 for the unowned case would confirm to a caller
     * that an exercise with that id exists.
     * @param ex The exception object thrown
     * @return Returns a {@link ResponseEntity} containing key information regarding the exception
     */
    @ExceptionHandler(ExerciseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExerciseNotFound(ExerciseNotFoundException ex, HttpServletRequest req) {
        log.warn("Exercise not available to caller. path={} {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        "EXERCISE_NOT_FOUND",
                        "No exercise with this id is available",
                        req.getRequestURI()
                ));
    }
}
