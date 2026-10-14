package de.phil.fitness.backend.exercise.dto;

import de.phil.fitness.backend.exercise.model.ExerciseType;
import de.phil.fitness.backend.exercise.model.TrackingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateExerciseRequest(
        @NotBlank @Size(max = 128) String name,
        @NotNull ExerciseType exerciseType,
        @NotNull TrackingType trackingType,
        @Size(max = 1000) String description,
        @Size(max = 4000) String instructions
) {}
