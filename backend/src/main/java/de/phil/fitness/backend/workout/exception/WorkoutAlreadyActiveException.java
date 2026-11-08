package de.phil.fitness.backend.workout.exception;

import de.phil.fitness.backend.common.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Thrown, when a workout is started while the user still has one in progress.
 */
public class WorkoutAlreadyActiveException extends ApiException {
    public WorkoutAlreadyActiveException(String message) {
        super(HttpStatus.CONFLICT, "WORKOUT_ALREADY_ACTIVE", "Another workout is still in progress", message);
    }
}
