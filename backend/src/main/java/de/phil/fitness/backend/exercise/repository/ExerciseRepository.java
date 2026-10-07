package de.phil.fitness.backend.exercise.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    /**
     * Returns specific exercise by its id, if the user requesting is either owner or the exercise is part of the global catalog.
     *
     * <p>An exercise owned by someone else is indistinguishable from a non-existent one here on purpose; both yield an
     * empty result so that the API can answer them alike.
     * @param userId User requesting the exercise
     * @param id Exercise id
     * @return singular exercise
     */
    @Query("""
        SELECT e FROM Exercise e
        WHERE e.id = :id
            AND e.deletedAt IS NULL
            AND (e.ownerUserId IS NULL OR e.ownerUserId = :userId)
        """)
    Optional<Exercise> findAvailableById(@Param("userId") Long userId, @Param("id") Long id);

    /**
     * Returns set of available IDs. Needed for creating new routine-exercises to validate referenced exercise.
     * @param userId User creating a new resource and therefore needing the list.
     * @param ids The chosen referenced exercises entries IDs.
     * @return Set of available IDs based on the entered IDs.
     */
    @Query("""
    SELECT e.id FROM Exercise e
    WHERE e.id IN :ids
        AND e.deletedAt IS NULL
        AND (e.ownerUserId IS NULL OR e.ownerUserId = :userId)
    """)
    Set<Long> findAvailableIds(@Param("userId") Long userId, @Param("ids") Collection<Long> ids);

}
