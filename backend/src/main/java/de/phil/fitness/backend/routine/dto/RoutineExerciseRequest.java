package de.phil.fitness.backend.routine.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record RoutineExerciseRequest(
        @NotNull Long exerciseId,
        @Positive Integer targetSets,
        @Positive Integer targetRepsMin,
        @Positive Integer targetRepsMax,
        @Positive Integer targetDurationSeconds,
        @Positive @Digits(integer = 6, fraction = 2) BigDecimal targetDistanceMeters
) {
    @AssertTrue(message = "targetRepsMin must not exceed targetRepsMax")
    public boolean isRepRangeValid() {
        return targetRepsMin == null || targetRepsMax == null || targetRepsMin <= targetRepsMax;
    }
}

