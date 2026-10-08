package de.phil.fitness.backend.routine.exception;

import de.phil.fitness.backend.common.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Thrown, when a specific preset-routine is called by Id, but no preset-routine exists under said Id.
 */
public class PresetRoutineNotFoundException extends ApiException {
    public PresetRoutineNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "PRESET_ROUTINE_NOT_FOUND", "No preset-routine with this id is available", message);
    }
}
