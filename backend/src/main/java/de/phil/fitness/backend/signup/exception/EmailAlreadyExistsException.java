package de.phil.fitness.backend.signup.exception;

/**
 * Thrown when user creation process fails due to user's email already being tied to existing user entry.
 */
public class EmailAlreadyExistsException extends RuntimeException{
    /**
     *
     * @param msg Message containing contextual information
     */
    public EmailAlreadyExistsException(String msg) {
        super(msg);
    }
}
