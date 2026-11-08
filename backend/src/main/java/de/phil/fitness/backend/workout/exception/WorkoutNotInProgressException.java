package de.phil.fitness.backend.workout.exception;

import de.phil.fitness.backend.common.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Thrown, when a workout is finished that is already finished.
 */
public class WorkoutNotInProgressException extends ApiException {
    public WorkoutNotInProgressException(String message) {
        super(HttpStatus.CONFLICT, "WORKOUT_NOT_IN_PROGRESS", "The workout is already finished", message);
    }
}
