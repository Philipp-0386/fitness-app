package de.phil.fitness.backend.account.exception;

/**
 * Thrown when the password confirming an account change does not match the stored one.
 */
public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String msg) {
        super(msg);
    }
}
