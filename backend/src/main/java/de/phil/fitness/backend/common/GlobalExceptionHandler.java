package de.phil.fitness.backend.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.phil.fitness.backend.auth.exception.AccessDeniedException;
import de.phil.fitness.backend.signup.exception.DefaultRoleNotFoundException;
import de.phil.fitness.backend.signup.exception.EmailAlreadyExistsException;
import de.phil.fitness.backend.signup.exception.UsernameAlreadyTaken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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
     * @param ex The exception object thrown
     * @return Returns a {@link ResponseEntity} containing key information regarding the exception
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex, HttpServletRequest req) {
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
