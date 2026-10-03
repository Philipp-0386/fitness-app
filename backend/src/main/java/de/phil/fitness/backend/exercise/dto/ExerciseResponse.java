package de.phil.fitness.backend.exercise.dto;

import de.phil.fitness.backend.exercise.model.ExerciseType;
import de.phil.fitness.backend.exercise.model.TrackingType;

/**
 * Exercise as returned to the client.
 *
 * @param id Identifier of the exercise
 * @param name Display name
 * @param exerciseType STRENGTH, CARDIO or MOBILITY
 * @param trackingType which values a logged set holds
 * @param description Short summary, may be null
 * @param custom True for the requesting user's own definition, false for a global catalog entry.
 *               The owning user's id is deliberately not exposed.
 */
public record ExerciseResponse(
        Long id,
        String name,
        ExerciseType exerciseType,
        TrackingType trackingType,
        String description,
        boolean custom
) {
}
