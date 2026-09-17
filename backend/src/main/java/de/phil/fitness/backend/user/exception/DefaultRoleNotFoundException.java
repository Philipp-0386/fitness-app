package de.phil.fitness.backend.user.exception;

/**
 * Thrown when user creation process fails due to the default role not being accessible.
 */
public class DefaultRoleNotFoundException extends RuntimeException {
    /**
     *
     * @param msg Message containing contextual information
     */
    public DefaultRoleNotFoundException(String msg) {
        super(msg);
    }
}
