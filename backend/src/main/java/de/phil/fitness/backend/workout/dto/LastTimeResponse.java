package de.phil.fitness.backend.workout.dto;

import java.time.Instant;
import java.util.List;

//sets of an exercise from the newest other workout in which it has at least one set
public record LastTimeResponse(
        Instant startedAt,
        List<WorkoutSetResponse> sets
) {}
