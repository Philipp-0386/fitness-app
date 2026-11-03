package de.phil.fitness.backend.workout.dto;

import de.phil.fitness.backend.exercise.dto.ExerciseSummary;

import java.math.BigDecimal;
import java.util.List;

//lastTime is null when the exercise was never logged before
public record WorkoutExerciseResponse(
        Long id,
        Integer orderIndex,
        ExerciseSummary exercise,
        Integer targetSets,
        Integer targetRepsMin,
        Integer targetRepsMax,
        Integer targetDurationSeconds,
        BigDecimal targetDistanceMeters,
        List<WorkoutSetResponse> sets,
        LastTimeResponse lastTime
) {}
