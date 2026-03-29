package de.phil.fitness.backend.common;

import de.phil.fitness.backend.signup.exception.DefaultRoleNotFoundException;
import de.phil.fitness.backend.signup.exception.EmailAlreadyExistsException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Handles exceptions globally.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles case of already existing emails during the sign-up process and creation of a new user.
     * @param ex Accepts the exception object
     * @return  Returns a {@link ResponseEntity} containing key information regarding the exception
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        409,
                        LocalDateTime.now()
                ));
    }
    /**
     * Handles the case of missing default role during user creation process.
     * @param ex Accepts the exception object
     * @return  Returns a {@link ResponseEntity} containing key information regarding the exception
     */
    @ExceptionHandler(DefaultRoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleDefaultRoleNotFound(DefaultRoleNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        ex.getMessage(),
                        500,
                        LocalDateTime.now()
                ));
    }
}

