package de.phil.fitness.backend.exercise.dto;

import de.phil.fitness.backend.exercise.model.ExerciseType;
import de.phil.fitness.backend.exercise.model.TrackingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * {@code exerciseType} and {@code trackingType} are bound as enums on purpose. An unknown value fails during
 * deserialization, so the service never sees a type the database would reject.
 *
 * @param exerciseType STRENGTH, CARDIO or MOBILITY
 * @param trackingType which values a logged set holds, see {@link TrackingType}
 * @param description Short summary, may be null
 * @param instructions How the exercise is performed, may be null
 */
public record ExerciseRequest(
        @NotBlank @Size(max = 128) String name,
        @NotNull ExerciseType exerciseType,
        @NotNull TrackingType trackingType,
        @Size(max = 1000) String description,
        @Size(max = 4000) String instructions
) {}
