package de.phil.fitness.backend.exercise.mapper;

import de.phil.fitness.backend.exercise.dto.ExerciseRequest;
import org.springframework.stereotype.Component;

import de.phil.fitness.backend.exercise.dto.ExerciseResponse;
import de.phil.fitness.backend.exercise.model.Exercise;

/**
 * Maps {@link Exercise} entities to their response representation, and vice versa.
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

    /**
     * @param req the submitted exercise
     * @param userId id of the user the new exercise belongs to
     * @return a transient entity (timestamps are assigned on persist)
     */
    public Exercise mapRequestToExerciseEntity(ExerciseRequest req, Long userId) {
        Exercise exercise = new Exercise();
        exercise.setOwnerUserId(userId);
        exercise.setName(req.name());
        exercise.setExerciseType(req.exerciseType());
        exercise.setDescription(req.description());
        exercise.setInstructions(req.instructions());
        return exercise;
    }
}
