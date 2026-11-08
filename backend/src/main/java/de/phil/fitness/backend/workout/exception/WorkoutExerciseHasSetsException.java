package de.phil.fitness.backend.workout.exception;

import de.phil.fitness.backend.common.ApiException;
import org.springframework.http.HttpStatus;

/**
 * Thrown, when an exercise entry with logged sets is swapped; the sets belong to the exercise they were logged on.
 */
public class WorkoutExerciseHasSetsException extends ApiException {
    public WorkoutExerciseHasSetsException(String message) {
        super(HttpStatus.CONFLICT, "WORKOUT_EXERCISE_HAS_SETS", "Exercises with logged sets cannot be swapped", message);
    }
}
