package de.phil.fitness.backend.workout;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Which earlier performance an exercise shows as its last time. Earlier workouts are finished through the API and
 * then moved into the past, so their order does not depend on the speed of the test.
 */
class WorkoutLastTimeTests extends WorkoutTestBase {

    /** Runs a workout from the routine, logs one set on the entry at this position, finishes it and ages it. */
    private void completedWorkout(String username, int routineId, int position, int reps, double weight, String age)
            throws Exception {
        String body = startWorkout(username, routineId);
        int id = workoutId(body);
        logSet(username, id, entryId(body, position), 1, reps, weight);
        finishWorkout(username, id);
        ageWorkout(id, age);
    }

    @Test
    @Transactional
    @DisplayName("an exercise never done before has no last time")
    void neverDoneHasNoLastTime() throws Exception {
        startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));

        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(SINA)))
                .andExpect(jsonPath("$.exercises[0].lastTime").value(nullValue()));
    }

    @Test
    @Transactional
    @DisplayName("the start response already carries the last time")
    void startCarriesLastTime() throws Exception {
        int routineId = createRoutine(SINA, "barbell-bench-press");
        completedWorkout(SINA, routineId, 0, 10, 80, "1 day");
        flushAndClear();

        mockMvc.perform(post("/backend/workouts")
                        .header("Authorization", tokenFor(SINA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"routineId\": " + routineId + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exercises[0].lastTime.sets", hasSize(1)))
                .andExpect(jsonPath("$.exercises[0].lastTime.sets[0].weightKg").value(80.0));
    }

    @Test
    @Transactional
    @DisplayName("the newest earlier workout wins")
    void newestWins() throws Exception {
        int routineId = createRoutine(SINA, "barbell-bench-press");
        completedWorkout(SINA, routineId, 0, 10, 80, "2 days");
        completedWorkout(SINA, routineId, 0, 5, 90, "1 day");
        startWorkout(SINA, routineId);

        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(SINA)))
                .andExpect(jsonPath("$.exercises[0].lastTime.sets", hasSize(1)))
                .andExpect(jsonPath("$.exercises[0].lastTime.sets[0].reps").value(5))
                .andExpect(jsonPath("$.exercises[0].lastTime.sets[0].weightKg").value(90.0));
    }

    @Test
    @Transactional
    @DisplayName("a newer workout in which the exercise has no sets does not hide the one before")
    void entriesWithoutSetsAreSkipped() throws Exception {
        int routineId = createRoutine(SINA, "barbell-bench-press", "lat-pulldown");
        completedWorkout(SINA, routineId, 0, 10, 80, "2 days");
        completedWorkout(SINA, routineId, 1, 12, 50, "1 day");
        startWorkout(SINA, routineId);

        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(SINA)))
                .andExpect(jsonPath("$.exercises[0].lastTime.sets[0].weightKg").value(80.0))
                .andExpect(jsonPath("$.exercises[1].lastTime.sets[0].weightKg").value(50.0));
    }

    @Test
    @Transactional
    @DisplayName("the last time is found across routines")
    void lastTimeCrossesRoutines() throws Exception {
        completedWorkout(SINA, createRoutine(SINA, "plank", "barbell-bench-press"), 1, 10, 80, "1 day");
        startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));

        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(SINA)))
                .andExpect(jsonPath("$.exercises[0].lastTime.sets[0].weightKg").value(80.0));
    }

    @Test
    @Transactional
    @DisplayName("sets of the running workout are not its own last time")
    void runningWorkoutIsExcluded() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        logSet(SINA, workoutId(body), entryId(body, 0), 1, 10, 80);

        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(SINA)))
                .andExpect(jsonPath("$.exercises[0].lastTime").value(nullValue()));
    }

    @Test
    @Transactional
    @DisplayName("the workouts of other users do not count")
    void otherUsersDoNotCount() throws Exception {
        completedWorkout(MARCO, createRoutine(MARCO, "barbell-bench-press"), 0, 10, 80, "1 day");
        startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));

        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(SINA)))
                .andExpect(jsonPath("$.exercises[0].lastTime").value(nullValue()));
    }

    @Test
    @Transactional
    @DisplayName("the last time lists every set of that workout in order, warm-ups included")
    void lastTimeListsAllSets() throws Exception {
        int routineId = createRoutine(SINA, "barbell-bench-press");
        String body = startWorkout(SINA, routineId);
        int id = workoutId(body);
        int entry = entryId(body, 0);
        logSet(SINA, id, entry, 2, 10, 80);
        putSet(SINA, id, UUID.randomUUID(), """
                {"workoutExerciseId": %d, "setNumber": 1, "setType": "WARMUP", "reps": 10, "weightKg": 40}
                """.formatted(entry)).andExpect(status().isOk());
        flushAndClear();
        finishWorkout(SINA, id);
        ageWorkout(id, "1 day");
        startWorkout(SINA, routineId);

        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(SINA)))
                .andExpect(jsonPath("$.exercises[0].lastTime.sets", hasSize(2)))
                .andExpect(jsonPath("$.exercises[0].lastTime.sets[0].setType").value("WARMUP"))
                .andExpect(jsonPath("$.exercises[0].lastTime.sets[1].setType").value("WORKING"));
    }

    @Test
    @Transactional
    @DisplayName("an added exercise brings its last time along")
    void addedExerciseHasLastTime() throws Exception {
        completedWorkout(SINA, createRoutine(SINA, "plank", "barbell-bench-press"), 1, 10, 80, "1 day");
        String body = startWorkout(SINA, createRoutine(SINA, "lat-pulldown"));

        mockMvc.perform(post("/backend/workouts/" + workoutId(body) + "/exercises")
                        .header("Authorization", tokenFor(SINA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"exerciseId\": " + exerciseId("barbell-bench-press") + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lastTime.sets[0].weightKg").value(80.0));
    }
}
