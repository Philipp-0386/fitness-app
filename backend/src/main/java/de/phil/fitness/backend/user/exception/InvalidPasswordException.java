package de.phil.fitness.backend.user.exception;

/**
 * Thrown when the password confirmation does not match the stored one.
 */
public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String msg) {
        super(msg);
    }
}
