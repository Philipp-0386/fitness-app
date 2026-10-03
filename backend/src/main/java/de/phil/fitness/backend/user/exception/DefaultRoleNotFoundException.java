package de.phil.fitness.backend.user.exception;

import org.springframework.http.HttpStatus;

import de.phil.fitness.backend.common.ApiException;

/**
 * Thrown when user creation process fails due to the default role not being accessible.
 */
public class DefaultRoleNotFoundException extends ApiException {
    /**
     *
     * @param msg Message containing contextual information
     */
    public DefaultRoleNotFoundException(String msg) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "DEFAULT_ROLE_NOT_FOUND", "Default role not found during user creation!", msg);
    }
}
