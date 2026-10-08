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
            SELECT ur FROM Routine ur
            WHERE ur.userId = :userId
            ORDER BY ur.name ASC
    """)
    List<Routine> getAllUserRoutinesByUserId(@Param("userId") Long userId);

    /**
     * Returns one specific user-owned routine.
     * @param userId Id of the requsting user.
     * @param id Id of the searched routine.
     * @return Singular user-owned routine.
     */
    @Query("""
            SELECT ur FROM Routine ur
            WHERE ur.id = :id AND ur.userId = :userId
    """)
    Optional<Routine> getUserRoutineById(@Param("userId") Long userId ,@Param("id") Long id);

    /**
     * Returns a specfic user-owned routine including the routine-exercises that are part of it.
     * @param userId Id of the requsting user.
     * @param id Id of the searched routine.
     * @return Singular user-owned routine including the referencing routine-exercises.
     */
    @Query("""
        SELECT ur FROM Routine ur
            LEFT JOIN FETCH ur.exercises
        WHERE ur.id = :id AND ur.userId = :userId
    """)
    Optional<Routine> getUserRoutineDetailedById(@Param("userId") Long userId ,@Param("id") Long id);

    /**
     * Returns all preset routines defined.
     * @return List of all preset routines.
     */
    @Query("""
            SELECT pr FROM Routine pr
            WHERE pr.userId IS NULL
            ORDER BY pr.name ASC
            """)
    List<Routine> getAllPresetRoutines();

    /**
     * Returns specific preset-routine by its Id.
     * @param id Id of the searched routine.
     * @return Singular preset-routine.
     */
    @Query("""
        SELECT pr FROM Routine pr
        WHERE pr.id = :id AND pr.userId IS NULL
    """)
    Optional<Routine> getPresetRoutineById(@Param("id") Long id);

    /**
     * Returns specific preset-routine by its Id including the referencing routine-exercises.
     * @param id Id of the searched routine.
     * @return Singular preset-routine.
     */
    @Query("""
        SELECT pr FROM Routine pr
            LEFT JOIN FETCH pr.exercises
        WHERE pr.id = :id AND pr.userId IS NULL
    """)
    Optional<Routine> getPresetRoutineDetailedById(@Param("id") Long id);
}
