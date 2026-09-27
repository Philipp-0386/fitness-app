package de.phil.fitness.backend.user.exception;

/**
 * Thrown when an email is already tied to another user. 
 * Thrown during both signup and update process. 
 */
public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String msg) {
        super(msg);
    }
}
