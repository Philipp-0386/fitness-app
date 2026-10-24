package de.phil.fitness.backend.exercise.dto;

/**
 * Area within a muscle group, e.g. Upper Chest within Chest.
 *
 * @param id Identifier of the area, matches {@link ExerciseMuscleResponse#id()}
 * @param name Display name
 */
public record MuscleAreaResponse(
        Long id,
        String name
) {}
