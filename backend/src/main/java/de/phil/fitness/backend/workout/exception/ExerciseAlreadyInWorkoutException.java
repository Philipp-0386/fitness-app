package de.phil.fitness.backend.workout.exception;

import de.phil.fitness.backend.common.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Thrown, when an exercise is added or swapped in that another entry of the workout already has.
 */
public class ExerciseAlreadyInWorkoutException extends ApiException {
    public ExerciseAlreadyInWorkoutException(String message) {
        super(HttpStatus.CONFLICT, "EXERCISE_ALREADY_IN_WORKOUT", "This exercise is already part of the workout", message);
    }
}
