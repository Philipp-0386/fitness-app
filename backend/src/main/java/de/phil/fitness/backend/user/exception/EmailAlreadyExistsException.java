package de.phil.fitness.backend.user.exception;

import org.springframework.http.HttpStatus;

import de.phil.fitness.backend.common.ApiException;

/**
 * Thrown when an email is already tied to another user.
 * Thrown during both signup and update process.
 */
public class EmailAlreadyExistsException extends ApiException {
    public EmailAlreadyExistsException(String msg) {
        super(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS", "User with this email already registered!", msg);
    }
}
