package de.phil.fitness.backend.workout.exception;

import de.phil.fitness.backend.common.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Thrown, when a workout is called by Id, but the user owns no workout under said Id.
 */
public class WorkoutNotFoundException extends ApiException {
    public WorkoutNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "WORKOUT_NOT_FOUND", "No workout with this id is available", message);
    }
}
