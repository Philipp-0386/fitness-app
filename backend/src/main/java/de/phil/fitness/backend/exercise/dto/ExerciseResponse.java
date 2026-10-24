package de.phil.fitness.backend.exercise.dto;

import de.phil.fitness.backend.exercise.model.ExerciseType;
import de.phil.fitness.backend.exercise.model.TrackingType;

import java.util.List;

/**
 * Exercise as returned to the client.
 *
 * @param id Identifier of the exercise
 * @param name Display name
 * @param exerciseType STRENGTH, CARDIO or MOBILITY
 * @param trackingType which values a logged set holds
 * @param description Short summary, may be null
 * @param instructions How the exercise is performed, may be null
 * @param muscles Muscle groups said exercise trains/affects
 * @param custom True for the requesting user's own definition, false for a global catalog entry.
 *               The owning user's id is deliberately not exposed.
 */
public record ExerciseResponse(
        Long id,
        String name,
        ExerciseType exerciseType,
        TrackingType trackingType,
        String description,
        String instructions,
        List<ExerciseMuscleResponse> muscles,
        boolean custom
) {
}
