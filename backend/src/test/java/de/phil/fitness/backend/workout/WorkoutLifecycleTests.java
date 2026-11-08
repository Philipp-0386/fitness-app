package de.phil.fitness.backend.workout;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Starting a workout from a routine or a program day, reading the running workout, the automatic close after four
 * hours without activity, finishing and discarding.
 */
class WorkoutLifecycleTests extends WorkoutTestBase {

    private String workoutStatus(int workoutId) {
        return jdbcTemplate.queryForObject("SELECT status FROM workout WHERE id = ?", String.class, workoutId);
    }

    private int workoutCount(int workoutId) {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM workout WHERE id = ?", Integer.class, workoutId);
    }

    // -----------------------------------------------------------------------
    // Start
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("a start copies the routine's exercises and targets in order, without sets")
    void startCopiesTheRoutine() throws Exception {
        long bench = exerciseId("barbell-bench-press");
        long plank = exerciseId("plank");
        String created = mockMvc.perform(post("/backend/routines")
                        .header("Authorization", tokenFor(SINA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Push", "exercises": [
                                  {"exerciseId": %d, "targetSets": 3, "targetRepsMin": 8, "targetRepsMax": 10},
                                  {"exerciseId": %d, "targetSets": 2, "targetDurationSeconds": 60}
                                ]}
                                """.formatted(bench, plank)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        int routineId = JsonPath.read(created, "$.id");
        flushAndClear();

        String body = mockMvc.perform(post("/backend/workouts")
                        .header("Authorization", tokenFor(SINA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"routineId\": " + routineId + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Push"))
                .andExpect(jsonPath("$.routineId").value(routineId))
                .andExpect(jsonPath("$.programDayId").value(nullValue()))
                .andExpect(jsonPath("$.exercises", hasSize(2)))
                .andExpect(jsonPath("$.exercises[0].orderIndex").value(0))
                .andExpect(jsonPath("$.exercises[0].exercise.id").value(bench))
                .andExpect(jsonPath("$.exercises[0].targetSets").value(3))
                .andExpect(jsonPath("$.exercises[0].targetRepsMin").value(8))
                .andExpect(jsonPath("$.exercises[0].targetRepsMax").value(10))
                .andExpect(jsonPath("$.exercises[0].sets", hasSize(0)))
                .andExpect(jsonPath("$.exercises[1].orderIndex").value(1))
                .andExpect(jsonPath("$.exercises[1].targetDurationSeconds").value(60))
                .andReturn().getResponse().getContentAsString();
        flushAndClear();

        assertThat(workoutStatus(workoutId(body))).isEqualTo("IN_PROGRESS");
    }

    @Test
    @Transactional
    @DisplayName("a start from a program day takes the day's routine and remembers the day")
    void startFromProgramDay() throws Exception {
        Map<String, Object> day = jdbcTemplate.queryForMap("""
                SELECT pd.id, pd.routine_id FROM program_day pd
                JOIN program p ON p.id = pd.program_id
                JOIN userdata u ON u.id = p.user_id
                WHERE u.username = 'max12' AND pd.routine_id IS NOT NULL
                LIMIT 1
                """);

        mockMvc.perform(post("/backend/workouts")
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"programDayId\": " + day.get("id") + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.routineId").value(day.get("routine_id")))
                .andExpect(jsonPath("$.programDayId").value(day.get("id")));
    }

    @Test
    @Transactional
    @DisplayName("a rest day cannot be started")
    void restDayIsNotStartable() throws Exception {
        Long restDay = jdbcTemplate.queryForObject("""
                SELECT pd.id FROM program_day pd
                JOIN program p ON p.id = pd.program_id
                JOIN userdata u ON u.id = p.user_id
                WHERE u.username = 'max12' AND pd.routine_id IS NULL
                LIMIT 1
                """, Long.class);

        mockMvc.perform(post("/backend/workouts")
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"programDayId\": " + restDay + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("REST_DAY_NOT_STARTABLE"));
    }

    @Test
    @Transactional
    @DisplayName("the program day of another user looks like a missing one")
    void foreignProgramDayIsNotFound() throws Exception {
        Long day = jdbcTemplate.queryForObject("""
                SELECT pd.id FROM program_day pd
                JOIN program p ON p.id = pd.program_id
                JOIN userdata u ON u.id = p.user_id
                WHERE u.username = 'max12'
                LIMIT 1
                """, Long.class);

        mockMvc.perform(post("/backend/workouts")
                        .header("Authorization", tokenFor(SINA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"programDayId\": " + day + "}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROGRAM_DAY_NOT_FOUND"));
    }

    @Test
    @Transactional
    @DisplayName("the routine of another user looks like a missing one")
    void foreignRoutineIsNotFound() throws Exception {
        int routineId = createRoutine(SINA, "barbell-bench-press");

        mockMvc.perform(post("/backend/workouts")
                        .header("Authorization", tokenFor(MARCO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"routineId\": " + routineId + "}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_ROUTINE_NOT_FOUND"));
    }

    @Test
    @Transactional
    @DisplayName("a start needs exactly one of routine and program day")
    void startNeedsExactlyOneSource() throws Exception {
        mockMvc.perform(post("/backend/workouts")
                        .header("Authorization", tokenFor(SINA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));

        mockMvc.perform(post("/backend/workouts")
                        .header("Authorization", tokenFor(SINA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"routineId\": 1, \"programDayId\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    @Transactional
    @DisplayName("a second start is rejected while a workout is running")
    void secondStartIsRejected() throws Exception {
        int routineId = createRoutine(SINA, "barbell-bench-press");
        startWorkout(SINA, routineId);

        mockMvc.perform(post("/backend/workouts")
                        .header("Authorization", tokenFor(SINA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"routineId\": " + routineId + "}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("WORKOUT_ALREADY_ACTIVE"));
    }

    @Test
    @DisplayName("workouts need a login")
    void workoutsNeedALogin() throws Exception {
        mockMvc.perform(get("/backend/workouts/active"))
                .andExpect(status().isUnauthorized());
    }

    // -----------------------------------------------------------------------
    // Running workout and automatic close
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("without a running workout the answer is 204")
    void noRunningWorkout() throws Exception {
        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(SINA)))
                .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    @DisplayName("the running workout comes with its logged sets")
    void runningWorkoutHasItsSets() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        logSet(SINA, workoutId(body), entryId(body, 0), 1, 10, 80);

        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(SINA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(workoutId(body)))
                .andExpect(jsonPath("$.exercises[0].sets", hasSize(1)))
                .andExpect(jsonPath("$.exercises[0].sets[0].reps").value(10))
                .andExpect(jsonPath("$.exercises[0].sets[0].weightKg").value(80.0));
    }

    @Test
    @Transactional
    @DisplayName("a stale workout with sets is completed at the time of its newest set")
    void staleWorkoutWithSetsIsCompleted() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        int id = workoutId(body);
        logSet(SINA, id, entryId(body, 0), 1, 10, 80);
        ageWorkout(id, "6 hours");
        ageSets(id, "5 hours");
        flushAndClear();

        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(SINA)))
                .andExpect(status().isNoContent());
        flushAndClear();

        Map<String, Object> row = jdbcTemplate.queryForMap("""
                SELECT w.status, w.ended_at, max(s.created_at) AS newest FROM workout w
                JOIN workout_exercise we ON we.workout_id = w.id
                JOIN workout_set s ON s.workout_exercise_id = we.id
                WHERE w.id = ?
                GROUP BY w.status, w.ended_at
                """, id);
        assertThat(row.get("status")).isEqualTo("COMPLETED");
        assertThat(((Timestamp) row.get("ended_at")).toInstant().truncatedTo(ChronoUnit.MILLIS))
                .isEqualTo(((Timestamp) row.get("newest")).toInstant().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    @Transactional
    @DisplayName("a stale workout without sets is deleted")
    void staleWorkoutWithoutSetsIsDeleted() throws Exception {
        int id = workoutId(startWorkout(SINA, createRoutine(SINA, "barbell-bench-press")));
        ageWorkout(id, "5 hours");
        flushAndClear();

        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(SINA)))
                .andExpect(status().isNoContent());
        flushAndClear();

        assertThat(workoutCount(id)).isZero();
    }

    @Test
    @Transactional
    @DisplayName("a recent set keeps an old workout running")
    void recentSetKeepsWorkoutRunning() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        int id = workoutId(body);
        logSet(SINA, id, entryId(body, 0), 1, 10, 80);
        ageWorkout(id, "5 hours");
        flushAndClear();

        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(SINA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    @Transactional
    @DisplayName("a workout younger than four hours stays running")
    void youngWorkoutStaysRunning() throws Exception {
        int id = workoutId(startWorkout(SINA, createRoutine(SINA, "barbell-bench-press")));
        ageWorkout(id, "3 hours 59 minutes");
        flushAndClear();

        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(SINA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    @Transactional
    @DisplayName("a stale workout does not block a new start")
    void staleWorkoutDoesNotBlockAStart() throws Exception {
        int routineId = createRoutine(SINA, "barbell-bench-press");
        int stale = workoutId(startWorkout(SINA, routineId));
        ageWorkout(stale, "5 hours");
        flushAndClear();

        int started = workoutId(startWorkout(SINA, routineId));

        assertThat(started).isNotEqualTo(stale);
        assertThat(workoutCount(stale)).isZero();
    }

    @Test
    @Transactional
    @DisplayName("the seeded stale workout of lena is closed on her first read")
    void seededStaleWorkoutIsClosed() throws Exception {
        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor("lena_lifts")))
                .andExpect(status().isNoContent());
        flushAndClear();

        Integer running = jdbcTemplate.queryForObject("""
                SELECT count(*) FROM workout w JOIN userdata u ON u.id = w.user_id
                WHERE u.username = 'lena_lifts' AND w.status = 'IN_PROGRESS'
                """, Integer.class);
        assertThat(running).isZero();
    }

    // -----------------------------------------------------------------------
    // Finish and discard
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("finishing completes the workout and frees the user for a new one")
    void finishCompletesTheWorkout() throws Exception {
        int routineId = createRoutine(SINA, "barbell-bench-press");
        String body = startWorkout(SINA, routineId);
        int id = workoutId(body);
        logSet(SINA, id, entryId(body, 0), 1, 10, 80);

        finishWorkout(SINA, id);

        Map<String, Object> row = jdbcTemplate.queryForMap("SELECT status, ended_at FROM workout WHERE id = ?", id);
        assertThat(row.get("status")).isEqualTo("COMPLETED");
        assertThat(row.get("ended_at")).isNotNull();
        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(SINA)))
                .andExpect(status().isNoContent());
        startWorkout(SINA, routineId);
    }

    @Test
    @Transactional
    @DisplayName("a workout without sets cannot be finished")
    void finishNeedsASet() throws Exception {
        int id = workoutId(startWorkout(SINA, createRoutine(SINA, "barbell-bench-press")));

        mockMvc.perform(post("/backend/workouts/" + id + "/finish").header("Authorization", tokenFor(SINA)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("WORKOUT_HAS_NO_SETS"));
    }

    @Test
    @Transactional
    @DisplayName("a finished workout cannot be finished again")
    void finishTwiceIsRejected() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        int id = workoutId(body);
        logSet(SINA, id, entryId(body, 0), 1, 10, 80);
        finishWorkout(SINA, id);

        mockMvc.perform(post("/backend/workouts/" + id + "/finish").header("Authorization", tokenFor(SINA)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("WORKOUT_NOT_IN_PROGRESS"));
    }

    @Test
    @Transactional
    @DisplayName("the workout of another user can neither be finished nor deleted")
    void foreignWorkoutIsNotFound() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        int id = workoutId(body);
        logSet(SINA, id, entryId(body, 0), 1, 10, 80);

        mockMvc.perform(post("/backend/workouts/" + id + "/finish").header("Authorization", tokenFor(MARCO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WORKOUT_NOT_FOUND"));
        mockMvc.perform(delete("/backend/workouts/" + id).header("Authorization", tokenFor(MARCO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WORKOUT_NOT_FOUND"));

        assertThat(workoutStatus(id)).isEqualTo("IN_PROGRESS");
    }

    @Test
    @Transactional
    @DisplayName("discarding deletes the workout with its exercises and sets")
    void discardDeletesEverything() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        int id = workoutId(body);
        logSet(SINA, id, entryId(body, 0), 1, 10, 80);

        mockMvc.perform(delete("/backend/workouts/" + id).header("Authorization", tokenFor(SINA)))
                .andExpect(status().isNoContent());
        flushAndClear();

        assertThat(workoutCount(id)).isZero();
        assertThat(setCount(id)).isZero();
    }
}
