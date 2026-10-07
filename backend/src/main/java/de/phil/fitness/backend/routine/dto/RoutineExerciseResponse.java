package de.phil.fitness.backend.routine.dto;

import de.phil.fitness.backend.exercise.dto.ExerciseSummary;

import java.math.BigDecimal;

public record RoutineExerciseResponse(
        Long id,
        Integer orderIndex,
        ExerciseSummary exercise,
        Integer targetSets,
        Integer targetRepsMin,
        Integer targetRepsMax,
        BigDecimal targetRpe,
        Integer targetDurationSeconds,
        BigDecimal targetDistanceMeters
) {
}
