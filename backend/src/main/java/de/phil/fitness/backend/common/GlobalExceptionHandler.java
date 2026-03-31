package de.phil.fitness.backend.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}

