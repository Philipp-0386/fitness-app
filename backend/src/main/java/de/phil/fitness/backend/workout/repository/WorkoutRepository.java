package de.phil.fitness.backend.workout.repository;

import de.phil.fitness.backend.workout.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout,Long> {
    /**
     * Returns a specific user-owned workout including its exercises, whatever its status.
     * @param userId Id of the requesting user.
     * @param id Id of the searched workout.
     * @return Singular user-owned workout, empty if the user owns no workout with this id.
     */
    @Query("""
        SELECT w FROM Workout w
            LEFT JOIN FETCH w.exercises
        WHERE w.userId = :userId
            AND w.id = :id
    """)
    Optional<Workout> getUserWorkoutById(@Param("userId") Long userId, @Param("id") Long id);

    /**
     * Returns the workout a user has in progress including its exercises. The unique index uq_workout_active allows
     * at most one.
     * @param userId Id of the requesting user.
     * @return The running workout, empty if the user has none.
     */
    @Query("""
        SELECT w FROM Workout w
            LEFT JOIN FETCH w.exercises
        WHERE w.userId = :userId
            AND w.status = de.phil.fitness.backend.workout.model.WorkoutStatus.IN_PROGRESS
    """)
    Optional<Workout> getActiveUserWorkout(@Param("userId") Long userId);

    /**
     * Returns when the newest set of a workout was logged, used to close a workout that has been inactive for too long.
     * Correcting a set later does not count, only its creation.
     * @param workoutId Id of the workout.
     * @return Creation time of the newest set, null if the workout has no sets.
     */
    @Query("""
        SELECT MAX(s.createdAt) FROM WorkoutSet s
            WHERE s.workoutExercise.workout.id = :workoutId
    """)
    Instant findNewestSetCreatedAt(@Param("workoutId") Long workoutId);
}
