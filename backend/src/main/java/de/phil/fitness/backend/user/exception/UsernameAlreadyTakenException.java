package de.phil.fitness.backend.user.exception;

import org.springframework.http.HttpStatus;

import de.phil.fitness.backend.common.ApiException;

/**
 * Throw when a username is already in use by another user.
 * Thrown during both signup and update process.
 */
public class UsernameAlreadyTakenException extends ApiException {
    public UsernameAlreadyTakenException(String message) {
        super(HttpStatus.CONFLICT, "USERNAME_ALREADY_TAKEN", "Username already taken!", message);
    }
}
