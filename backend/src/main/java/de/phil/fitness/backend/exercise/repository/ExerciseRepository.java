package de.phil.fitness.backend.exercise.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.phil.fitness.backend.exercise.model.Exercise;

/**
 * Repository for {@link Exercise} persistence operations.
 */
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    /**
     * Returns the exercises a user may pick from: the global catalog plus their own definitions.
     * @param userId id of the requesting user
     * @return the merged, alphabetically sorted list, excluding soft deleted rows
     */
    @Query("""
            SELECT e FROM Exercise e
            WHERE e.deletedAt IS NULL
              AND (e.ownerUserId IS NULL OR e.ownerUserId = :userId)
            ORDER BY e.name ASC
            """)
    List<Exercise> findAvailableTo(@Param("userId") Long userId);
}
