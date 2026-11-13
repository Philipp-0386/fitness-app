package de.phil.fitness.backend.workout;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The history: the list of finished workouts, page by page and newest first, and a single workout with its sets.
 * sina1's seed data holds one finished workout, "Shoulders" (2026-05-10), whose exercise was deleted later.
 */
class WorkoutHistoryTests extends WorkoutTestBase {

    /** Starts a workout from the routine, logs one set on its first exercise, finishes it and returns its id. */
    private int finishedWorkout(String username, int routineId) throws Exception {
        String body = startWorkout(username, routineId);
        int id = workoutId(body);
        logSet(username, id, entryId(body, 0), 1, 10, 80);
        finishWorkout(username, id);
        return id;
    }

    private int seedWorkoutId(String name) {
        return jdbcTemplate.queryForObject("SELECT id FROM workout WHERE name = ?", Integer.class, name);
    }

    // -----------------------------------------------------------------------
    // List
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("the list holds the finished workouts newest first, a running one is left out")
    void listsFinishedWorkoutsNewestFirst() throws Exception {
        int routine = createRoutine(SINA, "barbell-bench-press");
        int older = finishedWorkout(SINA, routine);
        ageWorkout(older, "2 days");
        int newer = finishedWorkout(SINA, routine);
        startWorkout(SINA, routine);

        mockMvc.perform(get("/backend/workouts").header("Authorization", tokenFor(SINA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workouts", hasSize(3)))
                .andExpect(jsonPath("$.workouts[0].id").value(newer))
                .andExpect(jsonPath("$.workouts[1].id").value(older))
                .andExpect(jsonPath("$.workouts[2].name").value("Shoulders"))
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @Transactional
    @DisplayName("an item counts all sets and names the exercises in their order, also those without sets")
    void itemCountsSetsAndNamesExercises() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press", "lat-pulldown"));
        int id = workoutId(body);
        logSet(SINA, id, entryId(body, 0), 1, 10, 80);
        logSet(SINA, id, entryId(body, 0), 2, 8, 80);
        finishWorkout(SINA, id);
        List<String> names = List.of(
                JsonPath.read(body, "$.exercises[0].exercise.name"),
                JsonPath.read(body, "$.exercises[1].exercise.name"));

        mockMvc.perform(get("/backend/workouts").header("Authorization", tokenFor(SINA)))
                .andExpect(jsonPath("$.workouts[0].id").value(id))
                .andExpect(jsonPath("$.workouts[0].setCount").value(2))
                .andExpect(jsonPath("$.workouts[0].exerciseNames", hasSize(2)))
                .andExpect(jsonPath("$.workouts[0].exerciseNames[0]").value(names.get(0)))
                .andExpect(jsonPath("$.workouts[0].exerciseNames[1]").value(names.get(1)))
                .andExpect(jsonPath("$.workouts[0].startedAt").value(notNullValue()))
                .andExpect(jsonPath("$.workouts[0].endedAt").value(notNullValue()));
    }

    @Test
    @Transactional
    @DisplayName("a deleted exercise keeps its name in old workouts")
    void deletedExerciseKeepsName() throws Exception {
        int shoulders = seedWorkoutId("Shoulders");

        mockMvc.perform(get("/backend/workouts").header("Authorization", tokenFor(SINA)))
                .andExpect(jsonPath("$.workouts", hasSize(1)))
                .andExpect(jsonPath("$.workouts[0].exerciseNames[0]").value("Old Smith Machine Press"))
                .andExpect(jsonPath("$.workouts[0].setCount").value(setCount(shoulders)));
    }

    @Test
    @Transactional
    @DisplayName("pages are cut by size and tell whether older workouts follow")
    void pagesTellWhetherMoreFollow() throws Exception {
        int routine = createRoutine(SINA, "barbell-bench-press");
        finishedWorkout(SINA, routine);
        finishedWorkout(SINA, routine);

        mockMvc.perform(get("/backend/workouts?page=0&size=2").header("Authorization", tokenFor(SINA)))
                .andExpect(jsonPath("$.workouts", hasSize(2)))
                .andExpect(jsonPath("$.hasNext").value(true));
        mockMvc.perform(get("/backend/workouts?page=1&size=2").header("Authorization", tokenFor(SINA)))
                .andExpect(jsonPath("$.workouts", hasSize(1)))
                .andExpect(jsonPath("$.workouts[0].name").value("Shoulders"))
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @Transactional
    @DisplayName("a page past the end is empty")
    void pagePastTheEndIsEmpty() throws Exception {
        mockMvc.perform(get("/backend/workouts?page=5").header("Authorization", tokenFor(SINA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workouts", hasSize(0)))
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @Transactional
    @DisplayName("the list holds only the caller's workouts")
    void listHoldsOnlyOwnWorkouts() throws Exception {
        finishedWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));

        mockMvc.perform(get("/backend/workouts").header("Authorization", tokenFor(MARCO)))
                .andExpect(jsonPath("$.workouts", hasSize(1)))
                .andExpect(jsonPath("$.workouts[0].name").value("Conditioning"));
    }

    @ParameterizedTest(name = "{0}")
    @Transactional
    @CsvSource({
            "page=-1, page",
            "size=0,  size",
            "size=51, size",
    })
    @DisplayName("a page or size out of range is rejected with the parameter")
    void invalidPagingIsRejected(String query, String parameter) throws Exception {
        mockMvc.perform(get("/backend/workouts?" + query).header("Authorization", tokenFor(SINA)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors." + parameter).exists());
    }

    @Test
    @DisplayName("the list needs a token")
    void listNeedsToken() throws Exception {
        mockMvc.perform(get("/backend/workouts"))
                .andExpect(status().isUnauthorized());
    }

    // -----------------------------------------------------------------------
    // Single workout
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("a finished workout comes with status, end, exercises and sets, without last time")
    void finishedWorkoutHasItsSets() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press", "lat-pulldown"));
        int id = workoutId(body);
        logSet(SINA, id, entryId(body, 0), 1, 10, 80);
        finishWorkout(SINA, id);

        mockMvc.perform(get("/backend/workouts/" + id).header("Authorization", tokenFor(SINA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.endedAt").value(notNullValue()))
                .andExpect(jsonPath("$.exercises", hasSize(2)))
                .andExpect(jsonPath("$.exercises[0].sets", hasSize(1)))
                .andExpect(jsonPath("$.exercises[0].sets[0].reps").value(10))
                .andExpect(jsonPath("$.exercises[1].sets", hasSize(0)))
                .andExpect(jsonPath("$.exercises[0].lastTime").value(nullValue()));
    }

    @Test
    @Transactional
    @DisplayName("a running workout can be read as well, without an end")
    void runningWorkoutCanBeRead() throws Exception {
        int id = workoutId(startWorkout(SINA, createRoutine(SINA, "barbell-bench-press")));

        mockMvc.perform(get("/backend/workouts/" + id).header("Authorization", tokenFor(SINA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.endedAt").value(nullValue()));
    }

    @Test
    @Transactional
    @DisplayName("a deleted exercise keeps its name in the single workout")
    void singleWorkoutKeepsDeletedExerciseName() throws Exception {
        mockMvc.perform(get("/backend/workouts/" + seedWorkoutId("Shoulders")).header("Authorization", tokenFor(SINA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exercises[0].exercise.name").value("Old Smith Machine Press"))
                .andExpect(jsonPath("$.exercises[0].exercise.deleted").value(true));
    }

    @Test
    @Transactional
    @DisplayName("another user's workout is not found")
    void foreignWorkoutIsNotFound() throws Exception {
        mockMvc.perform(get("/backend/workouts/" + seedWorkoutId("Shoulders")).header("Authorization", tokenFor(MARCO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WORKOUT_NOT_FOUND"));
    }

    @Test
    @Transactional
    @DisplayName("an unknown id is not found")
    void unknownWorkoutIsNotFound() throws Exception {
        mockMvc.perform(get("/backend/workouts/999999").header("Authorization", tokenFor(SINA)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WORKOUT_NOT_FOUND"));
    }
}
