package de.phil.fitness.backend.program.exception;

import de.phil.fitness.backend.common.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Thrown, when an update references a day by Id that does not belong to the updated program.
 */
public class ProgramDayNotFoundException extends ApiException {
    public ProgramDayNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "PROGRAM_DAY_NOT_FOUND", "No day with this id belongs to the program", message);
    }
}
