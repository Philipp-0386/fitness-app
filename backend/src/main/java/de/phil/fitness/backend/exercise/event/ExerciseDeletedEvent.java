package de.phil.fitness.backend.exercise.event;

public record ExerciseDeletedEvent(
        Long userId,
        Long exerciseId
) {}
