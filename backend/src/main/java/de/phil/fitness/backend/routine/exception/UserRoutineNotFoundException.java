package de.phil.fitness.backend.routine.exception;

import de.phil.fitness.backend.common.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Thrown, when a specific user-owned routine is called by Id, but no routine exists under said Id.
 */
public class UserRoutineNotFoundException extends ApiException {
    public UserRoutineNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "USER_ROUTINE_NOT_FOUND", "No user-routine with this id is available", message);
    }
}
