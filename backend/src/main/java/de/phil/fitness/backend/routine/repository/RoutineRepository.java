package de.phil.fitness.backend.routine.repository;

import de.phil.fitness.backend.routine.model.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    /**
     * Returns all user-made routines.
     * @param userId Id of the user requesting their routines.
     * @return List of all user routines.
     */
    @Query("""
            SELECT p FROM Routine p
            WHERE p.userId = :userId
            ORDER BY p.name ASC
    """)
    List<Routine> getAllUserRoutinesByUserId(@Param("userId") Long userId);

    /**
     * Returns one specific user-owned routine.
     * @param userId Id of the requsting user.
     * @param id Id of the searched routine.
     * @return Singular user-owned routine.
     */
    @Query("""
            SELECT p FROM Routine p
            WHERE p.id = :id AND p.userId = :userId
    """)
    Optional<Routine> getUserRoutineById(@Param("userId") Long userId ,@Param("id") Long id);

    /**
     * Returns all preset routines defined.
     * @return List of all preset routines.
     */
    @Query("""
            SELECT p FROM Routine p
            WHERE p.userId IS NULL
            ORDER BY p.name ASC
            """)
    List<Routine> getAllPresetRoutines();

    /**
     * Returns specific preset-routine by its Id.
     * @param id Id of the searched routine.
     * @return Singular preset-routine.
     */
    @Query("""
        SELECT p FROM Routine p
        WHERE p.id = :id AND p.userId IS NULL
    """)
    Optional<Routine> getPresetRoutineById(@Param("id") Long id);
}
