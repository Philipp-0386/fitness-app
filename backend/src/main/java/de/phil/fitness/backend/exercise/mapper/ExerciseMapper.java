package de.phil.fitness.backend.exercise.mapper;

import org.springframework.stereotype.Component;

import de.phil.fitness.backend.exercise.dto.ExerciseResponse;
import de.phil.fitness.backend.exercise.model.Exercise;

/**
 * Maps {@link Exercise} entities to their response representation.
 */
@Component
public class ExerciseMapper {

    /**
     * @param exercise the entity to map
     * @return the response DTO with ownership reduced to a boolean flag
     */
    public ExerciseResponse toResponse(Exercise exercise) {
        return new ExerciseResponse(
                exercise.getId(),
                exercise.getName(),
                exercise.getExerciseType(),
                exercise.getDescription(),
                exercise.getOwnerUserId() != null
        );
    }
}
