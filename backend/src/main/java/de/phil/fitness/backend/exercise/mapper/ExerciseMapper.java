package de.phil.fitness.backend.exercise.mapper;

import de.phil.fitness.backend.exercise.dto.*;
import de.phil.fitness.backend.exercise.model.ExerciseMuscle;
import de.phil.fitness.backend.exercise.model.ExerciseTranslation;
import de.phil.fitness.backend.exercise.model.MuscleGroup;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import de.phil.fitness.backend.exercise.model.Exercise;

import java.util.Comparator;

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
        String language = LocaleContextHolder.getLocale().getLanguage();
        boolean english = "en".equals(language);
        ExerciseTranslation translation = english ? null : exercise.getTranslations().get(language);
        return new ExerciseResponse(
                exercise.getId(),
                translation != null ? translation.getName() : exercise.getName(),
                exercise.getExerciseType(),
                exercise.getTrackingType(),
                translation != null ? translation.getDescription() : exercise.getDescription(),
                translation != null ? translation.getInstructions() : exercise.getInstructions(),
                exercise.getMuscles().stream()
                        .sorted(Comparator.comparing(ExerciseMuscle::getRole))
                        .map(muscle -> {
                            MuscleGroup group = muscle.getMuscleGroup();
                            return new ExerciseMuscleResponse(
                                    group.getId(),
                                    english ? group.getName() : group.getTranslations().getOrDefault(language, group.getName()),
                                    group.getParent() != null ? group.getParent().getId() : group.getId(),
                                    muscle.getRole());
                        })
                        .toList(),
                exercise.getOwnerUserId() != null
        );
    }

    /**
     * @param req the submitted exercise
     * @param userId id of the user the new exercise belongs to
     * @return a transient entity (timestamps are assigned on persist)
     */
    public Exercise mapRequestToExerciseEntity(CreateExerciseRequest req, Long userId) {
        Exercise exercise = new Exercise();
        exercise.setOwnerUserId(userId);
        exercise.setName(req.name());
        exercise.setExerciseType(req.exerciseType());
        exercise.setTrackingType(req.trackingType());
        exercise.setDescription(req.description());
        exercise.setInstructions(req.instructions());
        return exercise;
    }

    /**
     * Mapping for "routine" slice.
     * @param exercise The referencing exercises needed.
     * @return Compact DTO
     */
    public ExerciseSummary toSummary(Exercise exercise) {
        String language = LocaleContextHolder.getLocale().getLanguage();
        ExerciseTranslation translation = "en".equals(language) ? null : exercise.getTranslations().get(language);
        return new ExerciseSummary(
                exercise.getId(),
                translation != null ? translation.getName() : exercise.getName(),
                exercise.getTrackingType(),
                exercise.getDeletedAt() != null
        );
    }

    /**
     * Overwrites the editable fields of a managed entity. An omitted description or instruction is cleared.
     * @param exercise the entity to update
     * @param req the complete new state
     */
    public void applyUpdate(Exercise exercise, UpdateExerciseRequest req) {
        exercise.setName(req.name());
        exercise.setExerciseType(req.exerciseType());
        exercise.setTrackingType(req.trackingType());
        exercise.setDescription(req.description());
        exercise.setInstructions(req.instructions());
    }
}
