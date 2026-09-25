package de.phil.fitness.backend.user.exception;

/**
 * Thrown when no user exists for an id, typically the subject of a still valid access token whose
 * account was deleted.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String msg) {
        super(msg);
    }
}
