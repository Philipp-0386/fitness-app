package de.phil.fitness.backend.user.exception;

import org.springframework.http.HttpStatus;

import de.phil.fitness.backend.common.ApiException;

/**
 * Thrown when the password confirmation does not match the stored one.
 * Deliberately not 401. Caller is authenticated, only the confirmation failed.
 */
public class InvalidPasswordException extends ApiException {
    public InvalidPasswordException(String msg) {
        super(HttpStatus.FORBIDDEN, "INVALID_PASSWORD", "The password is incorrect", msg);
    }
}
