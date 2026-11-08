package de.phil.fitness.backend.workout.dto;

import jakarta.validation.constraints.NotNull;

public record WorkoutExerciseRequest(
        @NotNull Long exerciseId
) {}
