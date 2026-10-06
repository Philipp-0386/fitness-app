package de.phil.fitness.backend.routine.repository;

import de.phil.fitness.backend.routine.model.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    /**
     * Returns all preset routines defined.
     * @return list of all preset routines.
     */
    @Query("""
            SELECT p FROM Routine p
            WHERE p.userId IS NULL
            ORDER BY p.name ASC
            """)
    List<Routine> getAllPresetRoutines();
}
