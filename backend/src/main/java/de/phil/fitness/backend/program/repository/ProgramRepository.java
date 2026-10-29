package de.phil.fitness.backend.program.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import de.phil.fitness.backend.program.model.Program;
import org.springframework.data.jpa.repository.Modifying;
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

    /**
     * Checks whether a user owns a program with this id. Presets have no user and never match.
     * @param id Id of the program.
     * @param userId Id of the user.
     * @return true if the user owns the program.
     */
    boolean existsByIdAndUserId(Long id, Long userId);

    /**
     * Deactivates the active program of a user. Runs before {@link #activateUserProgram}, because the partial unique
     * index on the active program is checked row by row and cannot be deferred.
     * @param userId Id of the user.
     * @return Number of deactivated programs, 0 or 1.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
        UPDATE Program p SET p.active = FALSE
        WHERE p.userId = :userId AND p.active = TRUE
    """)
    int deactivateUserPrograms(@Param("userId") Long userId);

    /**
     * Activates a user-owned program. A bulk update leaves updatedAt alone, activating does not edit the plan.
     * The caller checks the ownership with {@link #existsByIdAndUserId} first, so a refused activation changes nothing.
     * @param userId Id of the requesting user.
     * @param id Id of the program to activate.
     * @return Number of activated programs.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
        UPDATE Program p SET p.active = TRUE
        WHERE p.id = :id AND p.userId = :userId
    """)
    int activateUserProgram(@Param("userId") Long userId, @Param("id") Long id);
}
