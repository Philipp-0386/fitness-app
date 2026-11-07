package de.phil.fitness.backend.workout.repository;

import java.time.Instant;

/**
 * Projection of {@link WorkoutRepository#findLastTimeEntries}: the newest earlier entry of an exercise that has sets.
 */
public interface LastTimeEntry {
    Long getExerciseId();

    Long getEntryId();

    Instant getStartedAt();
}
