package de.phil.fitness.backend.exercise.dto;

import java.util.List;

import de.phil.fitness.backend.exercise.model.BodyRegion;

/**
 * Top level muscle group with its areas, the options of the muscle group filter.
 *
 * @param id Identifier of the group, matches {@link ExerciseMuscleResponse#groupId()}
 * @param name Display name
 * @param bodyRegion UPPER, LOWER or CORE
 * @param areas Areas of the group, empty if it has none
 */
public record MuscleGroupResponse(
        Long id,
        String name,
        BodyRegion bodyRegion,
        List<MuscleAreaResponse> areas
) {}
