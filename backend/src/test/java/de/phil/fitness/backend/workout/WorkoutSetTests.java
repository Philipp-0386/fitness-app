package de.phil.fitness.backend.workout;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Logging, replacing and deleting sets: the client id makes a retry harmless, the values must fit the tracking type,
 * and sets of other users stay out of reach.
 */
class WorkoutSetTests extends WorkoutTestBase {

    // -----------------------------------------------------------------------
    // Client id
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("a new set is stored as a working set")
    void newSetIsAWorkingSet() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        UUID clientId = UUID.randomUUID();

        putSet(SINA, workoutId(body), clientId, setJson(entryId(body, 0), 1, 10, 80))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(clientId.toString()))
                .andExpect(jsonPath("$.setNumber").value(1))
                .andExpect(jsonPath("$.setType").value("WORKING"))
                .andExpect(jsonPath("$.reps").value(10))
                .andExpect(jsonPath("$.weightKg").value(80.0));
    }

    @Test
    @Transactional
    @DisplayName("set type and RPE are stored and can be changed back")
    void setTypeAndRpeAreStored() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        int id = workoutId(body);
        int entry = entryId(body, 0);
        UUID clientId = UUID.randomUUID();

        putSet(SINA, id, clientId, """
                {"workoutExerciseId": %d, "setNumber": 1, "setType": "WARMUP", "rpe": 7.5, "reps": 10, "weightKg": 40}
                """.formatted(entry))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.setType").value("WARMUP"))
                .andExpect(jsonPath("$.rpe").value(7.5));
        flushAndClear();

        putSet(SINA, id, clientId, setJson(entry, 1, 10, 40))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.setType").value("WORKING"))
                .andExpect(jsonPath("$.rpe").value(nullValue()));
    }

    @Test
    @Transactional
    @DisplayName("the same request twice leaves one set")
    void retryCreatesNoDuplicate() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        int id = workoutId(body);
        String json = setJson(entryId(body, 0), 1, 10, 80);
        UUID clientId = UUID.randomUUID();

        putSet(SINA, id, clientId, json).andExpect(status().isOk());
        flushAndClear();
        putSet(SINA, id, clientId, json).andExpect(status().isOk());
        flushAndClear();

        assertThat(setCount(id)).isEqualTo(1);
    }

    @Test
    @Transactional
    @DisplayName("a further request with the same client id replaces the values")
    void sameClientIdReplacesValues() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        int id = workoutId(body);
        int entry = entryId(body, 0);
        UUID clientId = logSet(SINA, id, entry, 1, 10, 80);

        putSet(SINA, id, clientId, setJson(entry, 1, 8, 82.5)).andExpect(status().isOk());
        flushAndClear();

        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(SINA)))
                .andExpect(jsonPath("$.exercises[0].sets", hasSize(1)))
                .andExpect(jsonPath("$.exercises[0].sets[0].reps").value(8))
                .andExpect(jsonPath("$.exercises[0].sets[0].weightKg").value(82.5));
    }

    @Test
    @Transactional
    @DisplayName("a set keeps its created time when it is replaced, so a correction is no new activity")
    void replacingKeepsCreatedAt() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        int id = workoutId(body);
        int entry = entryId(body, 0);
        UUID clientId = logSet(SINA, id, entry, 1, 10, 80);
        ageSets(id, "1 hour");
        flushAndClear();

        putSet(SINA, id, clientId, setJson(entry, 1, 8, 80)).andExpect(status().isOk());
        flushAndClear();

        Boolean stillOld = jdbcTemplate.queryForObject(
                "SELECT created_at < now() - interval '30 minutes' FROM workout_set WHERE client_id = ?",
                Boolean.class, clientId);
        assertThat(stillOld).isTrue();
    }

    @Test
    @Transactional
    @DisplayName("sets come back ordered by their number")
    void setsAreOrderedByNumber() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        int id = workoutId(body);
        int entry = entryId(body, 0);
        logSet(SINA, id, entry, 2, 9, 80);
        logSet(SINA, id, entry, 1, 10, 80);

        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(SINA)))
                .andExpect(jsonPath("$.exercises[0].sets[0].setNumber").value(1))
                .andExpect(jsonPath("$.exercises[0].sets[1].setNumber").value(2));
    }

    @Test
    @Transactional
    @DisplayName("a number another set of the exercise has is rejected")
    void takenNumberIsRejected() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        int id = workoutId(body);
        int entry = entryId(body, 0);
        logSet(SINA, id, entry, 1, 10, 80);

        putSet(SINA, id, UUID.randomUUID(), setJson(entry, 1, 8, 80))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("SET_NUMBER_TAKEN"));
    }

    @Test
    @Transactional
    @DisplayName("an existing set cannot move to another exercise")
    void existingSetCannotMove() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press", "lat-pulldown"));
        int id = workoutId(body);
        UUID clientId = logSet(SINA, id, entryId(body, 0), 1, 10, 80);

        putSet(SINA, id, clientId, setJson(entryId(body, 1), 1, 10, 80))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.workoutExerciseId").exists());
    }

    @Test
    @Transactional
    @DisplayName("a set can be corrected on a finished workout")
    void finishedWorkoutCanBeCorrected() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        int id = workoutId(body);
        int entry = entryId(body, 0);
        UUID clientId = logSet(SINA, id, entry, 1, 10, 80);
        finishWorkout(SINA, id);

        putSet(SINA, id, clientId, setJson(entry, 1, 8, 80))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reps").value(8));
    }

    // -----------------------------------------------------------------------
    // Values per tracking type
    // -----------------------------------------------------------------------

    @ParameterizedTest(name = "{0}: {1}")
    @Transactional
    @CsvSource(delimiter = '|', value = {
            "barbell-bench-press | \"reps\": 8, \"weightKg\": 80",
            "barbell-bench-press | \"reps\": 8, \"weightKg\": 0",
            "pull-up             | \"reps\": 8",
            "pull-up             | \"reps\": 8, \"weightKg\": 10",
            "assisted-pull-up    | \"reps\": 8, \"weightKg\": 30",
            "plank               | \"durationSeconds\": 60",
            "plank               | \"durationSeconds\": 60, \"weightKg\": 20",
            "farmers-walk        | \"weightKg\": 32, \"distanceMeters\": 40",
            "treadmill           | \"distanceMeters\": 5000, \"durationSeconds\": 1800",
    })
    @DisplayName("values that fit the tracking type are stored")
    void fittingValuesAreStored(String slug, String values) throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, slug));

        putSet(SINA, workoutId(body), UUID.randomUUID(),
                "{\"workoutExerciseId\": %d, \"setNumber\": 1, %s}".formatted(entryId(body, 0), values))
                .andExpect(status().isOk());
    }

    @ParameterizedTest(name = "{0}: {1} -> {2}")
    @Transactional
    @CsvSource(delimiter = '|', value = {
            "barbell-bench-press | \"reps\": 8                                          | weightKg        | required for tracking type WEIGHT_REPS",
            "barbell-bench-press | \"weightKg\": 80                                     | reps            | required for tracking type WEIGHT_REPS",
            "barbell-bench-press | \"reps\": 8, \"weightKg\": 80, \"durationSeconds\": 9 | durationSeconds | not used by tracking type WEIGHT_REPS",
            "barbell-bench-press | \"reps\": 8, \"weightKg\": 80, \"distanceMeters\": 9  | distanceMeters  | not used by tracking type WEIGHT_REPS",
            "pull-up             | \"weightKg\": 10                                     | reps            | required for tracking type BODYWEIGHT_REPS",
            "assisted-pull-up    | \"weightKg\": 30                                     | reps            | required for tracking type ASSISTED_REPS",
            "plank               | \"weightKg\": 20                                     | durationSeconds | required for tracking type DURATION",
            "plank               | \"durationSeconds\": 60, \"reps\": 10                | reps            | not used by tracking type DURATION",
            "farmers-walk        | \"weightKg\": 32                                     | distanceMeters  | required for tracking type WEIGHT_DISTANCE",
            "farmers-walk        | \"distanceMeters\": 40                               | weightKg        | required for tracking type WEIGHT_DISTANCE",
            "treadmill           | \"distanceMeters\": 5000                             | durationSeconds | required for tracking type DISTANCE_DURATION",
            "treadmill           | \"distanceMeters\": 5000, \"durationSeconds\": 9, \"weightKg\": 5 | weightKg | not used by tracking type DISTANCE_DURATION",
    })
    @DisplayName("values that do not fit the tracking type are rejected with the field")
    void unfittingValuesAreRejected(String slug, String values, String field, String message) throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, slug));

        putSet(SINA, workoutId(body), UUID.randomUUID(),
                "{\"workoutExerciseId\": %d, \"setNumber\": 1, %s}".formatted(entryId(body, 0), values))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors." + field).value(message));
    }

    @ParameterizedTest(name = "{0}")
    @Transactional
    @CsvSource(delimiter = '|', value = {
            "\"setNumber\": 0, \"reps\": 8, \"weightKg\": 80",
            "\"reps\": 8, \"weightKg\": 80",
            "\"setNumber\": 1, \"reps\": -1, \"weightKg\": 80",
            "\"setNumber\": 1, \"reps\": 8, \"weightKg\": -5",
            "\"setNumber\": 1, \"reps\": 8, \"weightKg\": 12345",
            "\"setNumber\": 1, \"reps\": 8, \"weightKg\": 80.123",
            "\"setNumber\": 1, \"reps\": 8, \"weightKg\": 80, \"rpe\": 0.5",
            "\"setNumber\": 1, \"reps\": 8, \"weightKg\": 80, \"rpe\": 10.5",
            "\"setNumber\": 1, \"reps\": 8, \"weightKg\": 80, \"rpe\": 8.25",
    })
    @DisplayName("malformed numbers are rejected before the tracking type is looked at")
    void malformedNumbersAreRejected(String values) throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));

        putSet(SINA, workoutId(body), UUID.randomUUID(),
                "{\"workoutExerciseId\": %d, %s}".formatted(entryId(body, 0), values))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    @Transactional
    @DisplayName("a client id that is no UUID is rejected")
    void malformedClientIdIsRejected() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));

        mockMvc.perform(put("/backend/workouts/" + workoutId(body) + "/sets/abc")
                        .header("Authorization", tokenFor(SINA))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(setJson(entryId(body, 0), 1, 10, 80)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PARAMETER"));
    }

    // -----------------------------------------------------------------------
    // Ownership
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("a set cannot be logged on the workout of another user")
    void foreignWorkoutIsNotFound() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));

        putSet(MARCO, workoutId(body), UUID.randomUUID(), setJson(entryId(body, 0), 1, 10, 80))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WORKOUT_NOT_FOUND"));
    }

    @Test
    @Transactional
    @DisplayName("an exercise entry of another workout is not accepted")
    void foreignEntryIsNotFound() throws Exception {
        String sina = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        String marco = startWorkout(MARCO, createRoutine(MARCO, "barbell-bench-press"));

        putSet(SINA, workoutId(sina), UUID.randomUUID(), setJson(entryId(marco, 0), 1, 10, 80))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WORKOUT_EXERCISE_NOT_FOUND"));
    }

    @Test
    @Transactional
    @DisplayName("the client id of another user's set looks like a missing workout and changes nothing")
    void foreignClientIdIsNotFound() throws Exception {
        String sina = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        String marco = startWorkout(MARCO, createRoutine(MARCO, "barbell-bench-press"));
        UUID clientId = logSet(SINA, workoutId(sina), entryId(sina, 0), 1, 10, 80);

        putSet(MARCO, workoutId(marco), clientId, setJson(entryId(marco, 0), 1, 1, 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WORKOUT_NOT_FOUND"));

        assertThat(jdbcTemplate.queryForObject(
                "SELECT reps FROM workout_set WHERE client_id = ?", Integer.class, clientId)).isEqualTo(10);
    }

    // -----------------------------------------------------------------------
    // Delete
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("deleting a set removes it, deleting it again still succeeds")
    void deleteIsRepeatable() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        int id = workoutId(body);
        UUID clientId = logSet(SINA, id, entryId(body, 0), 1, 10, 80);

        mockMvc.perform(delete("/backend/workouts/" + id + "/sets/" + clientId)
                        .header("Authorization", tokenFor(SINA)))
                .andExpect(status().isNoContent());
        flushAndClear();
        assertThat(setCount(id)).isZero();

        mockMvc.perform(delete("/backend/workouts/" + id + "/sets/" + clientId)
                        .header("Authorization", tokenFor(SINA)))
                .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    @DisplayName("a deleted set's number can be used again and the others keep theirs")
    void deleteDoesNotRenumber() throws Exception {
        String body = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        int id = workoutId(body);
        int entry = entryId(body, 0);
        UUID first = logSet(SINA, id, entry, 1, 10, 80);
        logSet(SINA, id, entry, 2, 9, 80);

        mockMvc.perform(delete("/backend/workouts/" + id + "/sets/" + first)
                        .header("Authorization", tokenFor(SINA)))
                .andExpect(status().isNoContent());
        flushAndClear();

        mockMvc.perform(get("/backend/workouts/active").header("Authorization", tokenFor(SINA)))
                .andExpect(jsonPath("$.exercises[0].sets", hasSize(1)))
                .andExpect(jsonPath("$.exercises[0].sets[0].setNumber").value(2));
        logSet(SINA, id, entry, 1, 8, 80);
        assertThat(setCount(id)).isEqualTo(2);
    }

    @Test
    @Transactional
    @DisplayName("the set of another workout is left alone and the answer does not reveal it")
    void deleteLeavesForeignSetAlone() throws Exception {
        String sina = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        String marco = startWorkout(MARCO, createRoutine(MARCO, "barbell-bench-press"));
        UUID clientId = logSet(SINA, workoutId(sina), entryId(sina, 0), 1, 10, 80);

        mockMvc.perform(delete("/backend/workouts/" + workoutId(marco) + "/sets/" + clientId)
                        .header("Authorization", tokenFor(MARCO)))
                .andExpect(status().isNoContent());
        flushAndClear();

        assertThat(setCount(workoutId(sina))).isEqualTo(1);
    }

    @Test
    @Transactional
    @DisplayName("sets of the workout of another user cannot be deleted through it")
    void deleteOnForeignWorkoutIsNotFound() throws Exception {
        String sina = startWorkout(SINA, createRoutine(SINA, "barbell-bench-press"));
        UUID clientId = logSet(SINA, workoutId(sina), entryId(sina, 0), 1, 10, 80);

        mockMvc.perform(delete("/backend/workouts/" + workoutId(sina) + "/sets/" + clientId)
                        .header("Authorization", tokenFor(MARCO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("WORKOUT_NOT_FOUND"));
    }
}
