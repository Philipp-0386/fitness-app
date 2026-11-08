package de.phil.fitness.backend.workout.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * The complete new order of a workout's exercise entries, every entry exactly once.
 */
public record WorkoutExerciseOrderRequest(
        @NotNull List<@NotNull Long> entryIds
) {}
