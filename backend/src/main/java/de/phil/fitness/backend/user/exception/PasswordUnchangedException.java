package de.phil.fitness.backend.user.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;

import de.phil.fitness.backend.common.ApiException;

/**
 * Thrown when a password update sets the password that is already stored.
 */
public class PasswordUnchangedException extends ApiException {
    public PasswordUnchangedException(String msg) {
        super(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Request body contains invalid fields", msg,
                Map.of("newPassword", "New password must differ from the current one"));
    }
}
