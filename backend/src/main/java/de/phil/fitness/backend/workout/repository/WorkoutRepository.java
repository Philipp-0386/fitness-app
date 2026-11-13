package de.phil.fitness.backend.workout.repository;

import de.phil.fitness.backend.workout.model.Workout;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout,Long> {

    /**
     * Returns a page of the user's finished workouts without their exercises.
     * @param userId Id of the requesting user.
     * @param pageable Page number and size.
     * @return The workouts of the page and whether more follow.
     */
    @Query("""
        SELECT w FROM Workout w
        WHERE w.userId = :userId
            AND w.status = de.phil.fitness.backend.workout.model.WorkoutStatus.COMPLETED
        ORDER BY w.startedAt DESC, w.id DESC
    """)
    Slice<Workout> findCompletedUserWorkouts(@Param("userId") Long userId, Pageable pageable);

    /**
     * Counts the sets of every entry of the given workouts. Collection can not be empty.
     * @param workoutIds Ids of the workouts.
     * @return One row per entry, ordered by workout and position.
     */
    @Query("""
        SELECT we.workout.id AS workoutId, we.exerciseId AS exerciseId, COUNT(s) AS setCount
        FROM WorkoutExercise we
            LEFT JOIN we.sets s
        WHERE we.workout.id IN :workoutIds
        GROUP BY we.workout.id, we.exerciseId, we.orderIndex
        ORDER BY we.workout.id, we.orderIndex
    """)
    List<WorkoutEntryCounts> countSetsPerEntry(@Param("workoutIds") Collection<Long> workoutIds);

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

    /**
     * Finds, per exercise, the newest entry in another workout of the user that has at least one set, across all
     * routines. Native for Postgres' DISTINCT ON. The caller must not pass an empty collection, IN () is invalid SQL.
     * @param userId Id of the requesting user.
     * @param workoutId Id of the workout asking, excluded from the search.
     * @param exerciseIds Ids of the exercises to look up.
     * @return At most one entry per exercise, exercises never done before are missing.
     */
    @Query(value = """
        SELECT DISTINCT ON (we.exercise_id)
               we.exercise_id AS "exerciseId", we.id AS "entryId", w.started_at AS "startedAt"
        FROM workout_exercise we
            JOIN workout w ON w.id = we.workout_id
        WHERE w.user_id = :userId
            AND w.id <> :workoutId
            AND we.exercise_id IN (:exerciseIds)
            AND EXISTS (SELECT 1 FROM workout_set s WHERE s.workout_exercise_id = we.id)
        ORDER BY we.exercise_id, w.started_at DESC, we.order_index
    """, nativeQuery = true)
    List<LastTimeEntry> findLastTimeEntries(@Param("userId") Long userId, @Param("workoutId") Long workoutId,
                                            @Param("exerciseIds") Collection<Long> exerciseIds);
}
