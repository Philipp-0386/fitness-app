package de.phil.fitness.backend.routine;

import com.jayway.jsonpath.JsonPath;
import de.phil.fitness.backend.TestDatabase;
import de.phil.fitness.backend.auth.JwtService;
import de.phil.fitness.backend.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Create, update and delete of own routines: the logic built on top of plain persistence, like the
 * order of exercises, replacing instead of appending, and what a delete takes along.
 *
 * <p>Every test creates the routine it works on, so no test depends on the seeded routines, and
 * runs in a transaction that is rolled back afterwards. Exercise ids are global exercises from the
 * V2 reference data: 1 Barbell Bench Press, 2 Incline Dumbbell Press, 38 Plank.
 *
 * <p>Within that transaction all requests share one persistence context, unlike the real
 * application where every request gets its own. Tests therefore call {@link #flushAndClear()}
 * between requests: before an update, so it loads the routine fresh as it would in production,
 * and before a read, so it shows what was actually stored instead of the entity in memory.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"dev", "test"})
@Import(TestDatabase.class)
class RoutineWriteTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long userId(String username) {
        return userRepository.findByUsername(username).orElseThrow().getId();
    }

    private String tokenFor(String username) {
        return "Bearer " + jwtService.generateAccessToken(userId(username).toString());
    }

    /** Creates a routine for max and returns the response body. */
    private String createRoutine(String json) throws Exception {
        return mockMvc.perform(post("/backend/routines")
                        .header("Authorization", tokenFor("max"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    /** Writes pending changes to the database and forgets all loaded entities, so the next read hits the database. */
    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    // -----------------------------------------------------------------------
    // Create
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("a routine without exercises can be created, the Location header points to it")
    void createWithoutExercises() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/backend/routines")
                        .header("Authorization", tokenFor("max"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"name": "Empty Routine"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exercises.length()").value(0))
                .andReturn().getResponse();
        Integer routineId = JsonPath.read(response.getContentAsString(), "$.id");

        assertThat(response.getHeader("Location")).endsWith("/backend/routines/" + routineId);
    }

    @Test
    @Transactional
    @DisplayName("the order of exercises follows the list, an orderIndex in the body is ignored")
    void createOrdersByListPosition() throws Exception {
        String created = createRoutine("""
                {
                  "name": "Ordered",
                  "exercises": [
                    { "exerciseId": 38, "orderIndex": 7, "targetSets": 3 },
                    { "exerciseId": 1, "orderIndex": 3, "targetSets": 3 }
                  ]
                }
                """);
        Integer routineId = JsonPath.read(created, "$.id");
        flushAndClear();

        mockMvc.perform(get("/backend/routines/" + routineId)
                        .header("Authorization", tokenFor("max")))
                .andExpect(jsonPath("$.exercises[0].exercise.id").value(38))
                .andExpect(jsonPath("$.exercises[0].orderIndex").value(0))
                .andExpect(jsonPath("$.exercises[1].exercise.id").value(1))
                .andExpect(jsonPath("$.exercises[1].orderIndex").value(1));
    }

    @Test
    @Transactional
    @DisplayName("the same exercise may appear twice with its own targets")
    void createAllowsSameExerciseTwice() throws Exception {
        String created = createRoutine("""
                {
                  "name": "Bench Twice",
                  "exercises": [
                    { "exerciseId": 1, "targetSets": 3, "targetRepsMin": 3, "targetRepsMax": 5 },
                    { "exerciseId": 1, "targetSets": 2, "targetRepsMin": 12, "targetRepsMax": 15 }
                  ]
                }
                """);
        Integer routineId = JsonPath.read(created, "$.id");
        flushAndClear();

        mockMvc.perform(get("/backend/routines/" + routineId)
                        .header("Authorization", tokenFor("max")))
                .andExpect(jsonPath("$.exercises.length()").value(2))
                .andExpect(jsonPath("$.exercises[0].targetRepsMax").value(5))
                .andExpect(jsonPath("$.exercises[1].targetRepsMax").value(15));
    }

    // -----------------------------------------------------------------------
    // Update
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("an update replaces the exercises instead of appending them")
    void updateReplacesExercises() throws Exception {
        String created = createRoutine("""
                {
                  "name": "Replace Me",
                  "exercises": [
                    { "exerciseId": 1, "targetSets": 3 },
                    { "exerciseId": 38, "targetSets": 3 }
                  ]
                }
                """);
        Integer routineId = JsonPath.read(created, "$.id");
        flushAndClear();

        mockMvc.perform(put("/backend/routines/" + routineId)
                        .header("Authorization", tokenFor("max"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "Replaced",
                          "exercises": [
                            { "exerciseId": 2, "targetSets": 4 }
                          ]
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exercises.length()").value(1))
                .andExpect(jsonPath("$.exercises[0].exercise.id").value(2))
                .andExpect(jsonPath("$.exercises[0].orderIndex").value(0))
                .andExpect(jsonPath("$.exercises[0].id").isNotEmpty());

        // uq_routine_exercise_order is deferred and would only be checked on commit, which a rolled
        // back test never reaches. Switching to immediate checks the pending rows right here.
        jdbcTemplate.execute("SET CONSTRAINTS ALL IMMEDIATE");
        flushAndClear();

        mockMvc.perform(get("/backend/routines/" + routineId)
                        .header("Authorization", tokenFor("max")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exercises.length()").value(1))
                .andExpect(jsonPath("$.exercises[0].exercise.id").value(2));
    }

    @Test
    @Transactional
    @DisplayName("updatedAt moves forward even if only the exercises change")
    void updateOfExercisesOnlyTouchesUpdatedAt() throws Exception {
        // Name and description stay the same, so the routine row itself has no change that would
        // trigger @PreUpdate.
        String created = createRoutine("""
                {
                  "name": "Same Name",
                  "exercises": [
                    { "exerciseId": 1, "targetSets": 3 }
                  ]
                }
                """);
        Integer routineId = JsonPath.read(created, "$.id");
        Instant before = Instant.parse(JsonPath.read(created, "$.updatedAt"));
        flushAndClear();

        String updated = mockMvc.perform(put("/backend/routines/" + routineId)
                        .header("Authorization", tokenFor("max"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "Same Name",
                          "exercises": [
                            { "exerciseId": 38, "targetSets": 3 }
                          ]
                        }
                        """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(Instant.parse(JsonPath.read(updated, "$.updatedAt"))).isAfter(before);
    }

    @Test
    @Transactional
    @DisplayName("an update clears what the body leaves out")
    void updateClearsOmittedFields() throws Exception {
        String created = createRoutine("""
                {
                  "name": "Full",
                  "description": "Will be gone.",
                  "exercises": [
                    { "exerciseId": 1, "targetSets": 3 }
                  ]
                }
                """);
        Integer routineId = JsonPath.read(created, "$.id");
        flushAndClear();

        mockMvc.perform(put("/backend/routines/" + routineId)
                        .header("Authorization", tokenFor("max"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"name": "Name Only"}
                        """))
                .andExpect(status().isOk());
        flushAndClear();

        mockMvc.perform(get("/backend/routines/" + routineId)
                        .header("Authorization", tokenFor("max")))
                .andExpect(jsonPath("$.name").value("Name Only"))
                .andExpect(jsonPath("$.description").value(nullValue()))
                .andExpect(jsonPath("$.exercises.length()").value(0));
    }

    @Test
    @Transactional
    @DisplayName("an invalid update body is reported per field")
    void updateWithInvalidBodyReportsFieldErrors() throws Exception {
        String created = createRoutine("""
                {"name": "Valid"}
                """);
        Integer routineId = JsonPath.read(created, "$.id");

        mockMvc.perform(put("/backend/routines/" + routineId)
                        .header("Authorization", tokenFor("max"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "exercises": [
                            { "exerciseId": 1, "targetRepsMin": 12, "targetRepsMax": 8 }
                          ]
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors['exercises[0].repRangeValid']").exists());
    }

    // -----------------------------------------------------------------------
    // Delete
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("a delete removes the routine together with its exercises")
    void deleteRemovesRoutineAndExercises() throws Exception {
        String created = createRoutine("""
                {
                  "name": "Delete Me",
                  "exercises": [
                    { "exerciseId": 1, "targetSets": 3 },
                    { "exerciseId": 38, "targetSets": 3 }
                  ]
                }
                """);
        Integer routineId = JsonPath.read(created, "$.id");

        mockMvc.perform(delete("/backend/routines/" + routineId)
                        .header("Authorization", tokenFor("max")))
                .andExpect(status().isNoContent());
        flushAndClear();

        mockMvc.perform(get("/backend/routines/" + routineId)
                        .header("Authorization", tokenFor("max")))
                .andExpect(status().isNotFound());
        assertThat(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM routine_exercise WHERE routine_id = ?", Long.class, routineId))
                .isZero();
    }

    @Test
    @Transactional
    @DisplayName("a workout keeps existing when its routine is deleted, only the reference is cleared")
    void deleteKeepsWorkouts() throws Exception {
        Map<String, Object> workout = jdbcTemplate.queryForMap("""
                SELECT w.id, w.routine_id, u.username FROM workout w
                JOIN userdata u ON u.id = w.user_id
                WHERE w.routine_id IS NOT NULL
                ORDER BY w.id
                LIMIT 1
                """);

        mockMvc.perform(delete("/backend/routines/" + workout.get("routine_id"))
                        .header("Authorization", tokenFor((String) workout.get("username"))))
                .andExpect(status().isNoContent());
        flushAndClear();

        Map<String, Object> after = jdbcTemplate.queryForMap(
                "SELECT routine_id FROM workout WHERE id = ?", workout.get("id"));
        assertThat(after.get("routine_id")).isNull();
    }
}
