package de.phil.fitness.backend.workout.exception;

import de.phil.fitness.backend.common.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Thrown, when a workout is finished without a single logged set. Such a workout is discarded instead.
 */
public class WorkoutHasNoSetsException extends ApiException {
    public WorkoutHasNoSetsException(String message) {
        super(HttpStatus.CONFLICT, "WORKOUT_HAS_NO_SETS", "A workout without logged sets cannot be finished", message);
    }
}
