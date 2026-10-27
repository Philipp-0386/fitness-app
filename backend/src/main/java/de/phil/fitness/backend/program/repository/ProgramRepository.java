package de.phil.fitness.backend.program.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import de.phil.fitness.backend.program.model.Program;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProgramRepository extends JpaRepository<Program, Long> {
    /**
     * Returns all user-made programs including their days.
     * @param userId Id of the user requesting their programs.
     * @return List of all user programs.
     */
    @Query("""
        SELECT p FROM Program p
            LEFT JOIN FETCH p.days
        WHERE p.userId = :userId
        ORDER BY p.name ASC
    """)
    List<Program> getAllUserProgramsByUserId(@Param("userId") Long userId);

    /**
     * Returns a specific user-owned program including its days.
     * @param userId Id of the requesting user.
     * @param id Id of the searched program.
     * @return Singular user-owned program.
     */
    @Query("""
        SELECT p FROM Program p
            LEFT JOIN FETCH p.days
        WHERE p.id = :id AND p.userId = :userId
    """)
    Optional<Program> getUserProgramDetailedById(@Param("userId") Long userId, @Param("id") Long id);

    /**
     * Returns the active program of a user including its days.
     * @param userId Id of the requesting user.
     * @return The active program, empty if the user has none.
     */
    @Query("""
        SELECT p FROM Program p
            LEFT JOIN FETCH p.days
        WHERE p.userId = :userId AND p.active = TRUE
    """)
    Optional<Program> getActiveUserProgram(@Param("userId") Long userId);

    /**
     * Returns all preset programs including their days.
     * @return List of all preset programs.
     */
    @Query("""
        SELECT p FROM Program p
            LEFT JOIN FETCH p.days
        WHERE p.userId IS NULL
        ORDER BY p.name ASC
    """)
    List<Program> getAllPresetPrograms();

    /**
     * Returns a specific preset program including its days.
     * @param id Id of the searched program.
     * @return Singular preset program.
     */
    @Query("""
        SELECT p FROM Program p
            LEFT JOIN FETCH p.days
        WHERE p.id = :id AND p.userId IS NULL
    """)
    Optional<Program> getPresetProgramDetailedById(@Param("id") Long id);

    /**
     * Counts the programs of a user, used to enforce the program limit.
     * @param userId Id of the user.
     * @return Number of programs owned by the user.
     */
    long countByUserId(Long userId);
}
