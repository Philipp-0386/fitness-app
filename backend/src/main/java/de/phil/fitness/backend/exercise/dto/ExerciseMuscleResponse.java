package de.phil.fitness.backend.exercise.dto;

import de.phil.fitness.backend.exercise.model.MuscleRole;

/**
 *
 * @param id Id of muscle group
 * @param name Name of muscle group
 * @param groupId id of the top level group, the area's parent or the group itself
 * @param role Role for corresponding muscle group
 */
public record ExerciseMuscleResponse(
        Long id,
        String name,
        Long groupId, //filter for frontend
        MuscleRole role
) {}

