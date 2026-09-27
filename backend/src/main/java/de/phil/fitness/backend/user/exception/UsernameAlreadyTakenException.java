package de.phil.fitness.backend.user.exception;

/**
 * Throw when a username is already in use by another user. 
 * Thrown during both signup and update process.
 */
public class UsernameAlreadyTakenException extends RuntimeException{
    public UsernameAlreadyTakenException(String message) {
        super(message);
    }
}
