package de.phil.fitness.backend.program.exception;

import de.phil.fitness.backend.common.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Thrown, when a specific preset-program is called by Id, but no preset-program exists under said Id.
 */
public class PresetProgramNotFoundException extends ApiException {
    public PresetProgramNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "PRESET_PROGRAM_NOT_FOUND", "No preset-program with this id is available", message);
    }
}
