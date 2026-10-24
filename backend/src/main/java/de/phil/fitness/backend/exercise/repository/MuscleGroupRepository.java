package de.phil.fitness.backend.exercise.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.phil.fitness.backend.exercise.model.MuscleGroup;

/**
 * Read access to the {@link MuscleGroup} lookup.
 */
public interface MuscleGroupRepository extends JpaRepository<MuscleGroup, Long> {

    /**
     * @return groups and areas in one list, in the order the reference data inserted them
     */
    List<MuscleGroup> findAllByOrderByIdAsc();
}
