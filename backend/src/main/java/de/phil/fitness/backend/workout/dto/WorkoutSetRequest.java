package de.phil.fitness.backend.workout.dto;

import de.phil.fitness.backend.workout.model.SetType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record WorkoutSetRequest(
        @NotNull Long workoutExerciseId,
        @NotNull @Positive Integer setNumber,
        SetType setType,
        @PositiveOrZero Integer reps,
        @PositiveOrZero @Digits(integer = 4, fraction = 2) BigDecimal weightKg,
        @Positive Integer durationSeconds,
        @PositiveOrZero @Digits(integer = 6, fraction = 2) BigDecimal distanceMeters
) {}
