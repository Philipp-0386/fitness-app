package de.phil.fitness.backend.exercise.dto;

import de.phil.fitness.backend.exercise.model.MuscleRole;
import jakarta.validation.constraints.NotNull;

public record ExerciseMuscleRequest(
        @NotNull Long muscleGroupId,
        @NotNull MuscleRole role
) {}
