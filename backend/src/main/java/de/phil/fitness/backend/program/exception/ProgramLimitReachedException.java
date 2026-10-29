package de.phil.fitness.backend.program.exception;

import de.phil.fitness.backend.common.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Thrown, when a user tries to create a program but already owns the maximum number of programs.
 */
public class ProgramLimitReachedException extends ApiException {
    public ProgramLimitReachedException(String message) {
        super(HttpStatus.CONFLICT, "PROGRAM_LIMIT_REACHED", "The maximum number of programs is reached", message);
    }
}
