package de.phil.fitness.backend.workout;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Deviating from the plan during a workout: adding, swapping, removing and reordering its exercises.
 */
class WorkoutExerciseTests extends WorkoutTestBase {

    private ResultActions add(String username, int workoutId, long exerciseId) throws Exception {
        return mockMvc.perform(post("/backend/workouts/" + workoutId + "/exercises")
                .header("Authorization", tokenFor(username))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"exerciseId\": " + exerciseId + "}"));
    }

    private ResultActions swap(String username, int workoutId, int entryId, long exerciseId) throws Exception {
        return mockMvc.perform(put("/backend/workouts/" + workoutId + "/exercises/" + entryId)
                .header("Authorization", tokenFor(username))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"exerciseId\": " + exerciseId + "}"));
    }

    private ResultActions remove(String username, int workoutId, int entryId) throws Exception {
        return mockMvc.perform(delete("/backend/workouts/" + workoutId + "/exercises/" + entryId)
                .header("Authorization", tokenFor(username)));
    }

    private ResultActions reorder(String username, int workoutId, List<Integer> entryIds) throws Exception {
        return mockMvc.perform(put("/backend/workouts/" + workoutId + "/exercises/order")
                .header("Authorization", tokenFor(username))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"entryIds\": " + entryIds + "}"));
    }

    /** The entry ids of the running workout, in the order the API returns them. */
    private List<Integer> activeEntryIds(String username) throws Exception {
        String body = mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(username)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.exercises[*].id");
    }

    // -----------------------------------------------------------------------
    // Add
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("an added exercise comes last, has an id and no targets")
    void addAppends() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press", "lat-pulldown"));
        long plank = exerciseId("plank");

        add(SINA, workoutId(body), plank)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.orderIndex").value(2))
                .andExpect(jsonPath("$.exercise.id").value(plank))
                .andExpect(jsonPath("$.targetSets").value(nullValue()))
                .andExpect(jsonPath("$.sets", hasSize(0)))
                .andExpect(jsonPath("$.lastTime").value(nullValue()));
        flushAndClear();

        assertThat(activeEntryIds(SINA)).hasSize(3);
    }

    @Test
    @Transactional
    @DisplayName("an added exercise goes behind the highest position, also after a removal left a gap")
    void addGoesBehindTheHighestPosition() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press", "lat-pulldown", "plank"));
        int id = workoutId(body);
        remove(SINA, id, entryId(body, 1)).andExpect(status().isNoContent());
        flushAndClear();

        add(SINA, id, exerciseId("dead-hang"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderIndex").value(3));
    }

    @Test
    @Transactional
    @DisplayName("an exercise already in the workout cannot be added")
    void addRejectsDuplicates() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));

        add(SINA, workoutId(body), exerciseId("barbell-bench-press"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EXERCISE_ALREADY_IN_WORKOUT"));
    }

    @Test
    @Transactional
    @DisplayName("an unknown exercise and the own exercise of another user cannot be added")
    void addRejectsUnavailableExercises() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        Long foreign = jdbcTemplate.queryForObject("""
                SELECT e.id FROM exercise e JOIN userdata u ON u.id = e.owner_user_id
                WHERE u.username = 'lena_lifts' AND e.deleted_at IS NULL
                LIMIT 1
                """, Long.class);

        add(SINA, workoutId(body), 999999)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EXERCISE_NOT_FOUND"));
        add(SINA, workoutId(body), foreign)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EXERCISE_NOT_FOUND"));
    }

    @Test
    @Transactional
    @DisplayName("a soft deleted own exercise cannot be added")
    void addRejectsDeletedExercise() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        Long deleted = jdbcTemplate.queryForObject("""
                SELECT e.id FROM exercise e JOIN userdata u ON u.id = e.owner_user_id
                WHERE u.username = 'sina1' AND e.deleted_at IS NOT NULL
                LIMIT 1
                """, Long.class);

        add(SINA, workoutId(body), deleted)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EXERCISE_NOT_FOUND"));
    }

    @Test
    @Transactional
    @DisplayName("nothing can be added to the workout of another user")
    void addOnForeignWorkoutIsNotFound() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));

        add(MARCO, workoutId(body), exerciseId("plank"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WORKOUT_NOT_FOUND"));
    }

    // -----------------------------------------------------------------------
    // Swap
    // -----------------------------------------------------------------------

    /** Starts a workout from a routine with bench press (3 x 8-10) and lat pulldown. */
    private String startWithRepTargets() throws Exception {
        String created = mockMvc.perform(post("/backend/routines")
                        .header("Authorization", tokenFor(SINA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Reps", "exercises": [
                                  {"exerciseId": %d, "targetSets": 3, "targetRepsMin": 8, "targetRepsMax": 10},
                                  {"exerciseId": %d, "targetSets": 3}
                                ]}
                                """.formatted(exerciseId("barbell-bench-press"), exerciseId("lat-pulldown"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        flushAndClear();
        return startWorkout(SINA, JsonPath.read(created, "$.id"));
    }

    @Test
    @Transactional
    @DisplayName("a swap keeps position and targets when the tracking type uses them")
    void swapKeepsUsableTargets() throws Exception {
        String body = startWithRepTargets();
        long incline = exerciseId("incline-dumbbell-press");

        swap(SINA, workoutId(body), entryId(body, 0), incline)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(entryId(body, 0)))
                .andExpect(jsonPath("$.orderIndex").value(0))
                .andExpect(jsonPath("$.exercise.id").value(incline))
                .andExpect(jsonPath("$.targetSets").value(3))
                .andExpect(jsonPath("$.targetRepsMin").value(8))
                .andExpect(jsonPath("$.targetRepsMax").value(10));
    }

    @Test
    @Transactional
    @DisplayName("a swap drops the targets the new tracking type does not use, the set target stays")
    void swapDropsUnusableTargets() throws Exception {
        String body = startWithRepTargets();

        swap(SINA, workoutId(body), entryId(body, 0), exerciseId("plank"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetSets").value(3))
                .andExpect(jsonPath("$.targetRepsMin").value(nullValue()))
                .andExpect(jsonPath("$.targetRepsMax").value(nullValue()));
        flushAndClear();

        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(SINA)))
                .andExpect(jsonPath("$.exercises[0].targetRepsMin").value(nullValue()));
    }

    @Test
    @Transactional
    @DisplayName("an exercise with logged sets cannot be swapped")
    void swapRejectsLoggedSets() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        int id = workoutId(body);
        logSet(SINA, id, entryId(body, 0), 1, 10, 80);

        swap(SINA, id, entryId(body, 0), exerciseId("plank"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("WORKOUT_EXERCISE_HAS_SETS"));
    }

    @Test
    @Transactional
    @DisplayName("a swap to an exercise another entry already has is rejected")
    void swapRejectsDuplicates() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press", "plank"));

        swap(SINA, workoutId(body), entryId(body, 0), exerciseId("plank"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EXERCISE_ALREADY_IN_WORKOUT"));
    }

    @Test
    @Transactional
    @DisplayName("swapping an entry to its own exercise is allowed")
    void swapToSameExerciseIsAllowed() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));

        swap(SINA, workoutId(body), entryId(body, 0), exerciseId("barbell-bench-press"))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @DisplayName("an entry of another workout cannot be swapped")
    void swapOfForeignEntryIsNotFound() throws Exception {
        String sina = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        String marco = startWorkout(MARCO, createRoutine(MARCO, "barbell-bench-press"));

        swap(SINA, workoutId(sina), entryId(marco, 0), exerciseId("plank"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WORKOUT_EXERCISE_NOT_FOUND"));
    }

    @Test
    @Transactional
    @DisplayName("a swap to an unavailable exercise is rejected")
    void swapRejectsUnavailableExercise() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));

        swap(SINA, workoutId(body), entryId(body, 0), 999999)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EXERCISE_NOT_FOUND"));
    }

    // -----------------------------------------------------------------------
    // Remove
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("removing an entry takes its sets along and leaves the others in place")
    void removeTakesSetsAlong() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press", "lat-pulldown"));
        int id = workoutId(body);
        logSet(SINA, id, entryId(body, 0), 1, 10, 80);

        remove(SINA, id, entryId(body, 0)).andExpect(status().isNoContent());
        flushAndClear();

        assertThat(activeEntryIds(SINA)).containsExactly(entryId(body, 1));
        assertThat(setCount(id)).isZero();
    }

    @Test
    @Transactional
    @DisplayName("an entry of another workout cannot be removed")
    void removeOfForeignEntryIsNotFound() throws Exception {
        String sina = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        String marco = startWorkout(MARCO, createRoutine(MARCO, "barbell-bench-press"));

        remove(SINA, workoutId(sina), entryId(marco, 0))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WORKOUT_EXERCISE_NOT_FOUND"));
    }

    // -----------------------------------------------------------------------
    // Reorder
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("a reorder sets the new positions and satisfies the unique order constraint")
    void reorderSetsPositions() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press", "lat-pulldown", "plank"));
        int id = workoutId(body);
        List<Integer> reversed = List.of(entryId(body, 2), entryId(body, 1), entryId(body, 0));

        reorder(SINA, id, reversed).andExpect(status().isNoContent());
        flushAndClear();
        // The test transaction never commits, so the deferred constraint is checked here.
        jdbcTemplate.execute("SET CONSTRAINTS ALL IMMEDIATE");

        assertThat(activeEntryIds(SINA)).containsExactlyElementsOf(reversed);
        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(SINA)))
                .andExpect(jsonPath("$.exercises[0].orderIndex").value(0))
                .andExpect(jsonPath("$.exercises[2].orderIndex").value(2));
    }

    @Test
    @Transactional
    @DisplayName("entries with sets can be moved too")
    void reorderMovesEntriesWithSets() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press", "lat-pulldown"));
        int id = workoutId(body);
        logSet(SINA, id, entryId(body, 0), 1, 10, 80);

        reorder(SINA, id, List.of(entryId(body, 1), entryId(body, 0))).andExpect(status().isNoContent());
        flushAndClear();

        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(SINA)))
                .andExpect(jsonPath("$.exercises[1].sets", hasSize(1)));
    }

    @Test
    @Transactional
    @DisplayName("an order with a missing, doubled or foreign entry is rejected")
    void reorderRejectsIncompleteOrders() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press", "lat-pulldown"));
        int id = workoutId(body);
        int first = entryId(body, 0);
        int second = entryId(body, 1);

        for (List<Integer> order : List.of(List.of(first), List.of(first, first), List.of(first, 999999),
                List.of(first, second, 999999))) {
            reorder(SINA, id, order)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                    .andExpect(jsonPath("$.fieldErrors.entryIds").exists());
        }
    }

    @Test
    @Transactional
    @DisplayName("the workout of another user cannot be reordered")
    void reorderOfForeignWorkoutIsNotFound() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));

        reorder(MARCO, workoutId(body), List.of(entryId(body, 0)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WORKOUT_NOT_FOUND"));
    }
}
