package de.phil.fitness.backend.workout.dto;

import java.util.List;

public record WorkoutHistoryResponse(
        List<WorkoutListItemResponse> workouts,
        boolean hasNext
) {}
