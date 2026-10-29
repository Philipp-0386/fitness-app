package de.phil.fitness.backend.program.exception;

import de.phil.fitness.backend.common.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Thrown, when a specific user-owned program is called by Id, but no program exists under said Id.
 */
public class UserProgramNotFoundException extends ApiException {
    public UserProgramNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "USER_PROGRAM_NOT_FOUND", "No user-program with this id is available", message);
    }
}
