package de.phil.fitness.backend.workout;

import com.jayway.jsonpath.JsonPath;
import de.phil.fitness.backend.TestDatabase;
import de.phil.fitness.backend.auth.JwtService;
import de.phil.fitness.backend.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Shared setup of the workout tests. Every test creates the routines and workouts it needs through the API and runs
 * in a transaction that is rolled back afterwards. Exercises are global ones from the V2 reference data, looked up by
 * slug. Seeded users: sina1 and marco have no running workout, lena_lifts has a stale one.
 *
 * <p>Within that transaction all requests share one persistence context, unlike the real application where every
 * request gets its own. The helpers therefore call {@link #flushAndClear()} after a successful write, so the next
 * read shows what was actually stored.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"dev", "test"})
@Import(TestDatabase.class)
abstract class WorkoutTestBase {

    protected static final String SINA = "sina1";
    protected static final String MARCO = "marco";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    protected String tokenFor(String username) {
        Long id = userRepository.findByUsernameIgnoreCase(username).orElseThrow().getId();
        return "Bearer " + jwtService.generateAccessToken(id.toString());
    }

    /** Writes pending changes to the database and forgets all loaded entities, so the next read hits the database. */
    protected void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    protected long exerciseId(String slug) {
        return jdbcTemplate.queryForObject(
                "SELECT id FROM exercise WHERE slug = ? AND owner_user_id IS NULL", Long.class, slug);
    }

    /** Creates a routine with the given exercises, three target sets each, and returns its id. */
    protected int createRoutine(String username, String... slugs) throws Exception {
        String exercises = Arrays.stream(slugs)
                .map(slug -> "{\"exerciseId\": %d, \"targetSets\": 3}".formatted(exerciseId(slug)))
                .collect(Collectors.joining(","));
        String body = mockMvc.perform(post("/backend/routines")
                        .header("Authorization", tokenFor(username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Routine\", \"exercises\": [" + exercises + "]}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        flushAndClear();
        return JsonPath.read(body, "$.id");
    }

    /** Starts a workout from the routine and returns the response body. */
    protected String startWorkout(String username, int routineId) throws Exception {
        String body = mockMvc.perform(post("/backend/workouts")
                        .header("Authorization", tokenFor(username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"routineId\": " + routineId + "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        flushAndClear();
        return body;
    }

    protected int workoutId(String workoutBody) {
        return JsonPath.read(workoutBody, "$.id");
    }

    /** Id of the workout exercise entry at this position of a workout response. */
    protected int entryId(String workoutBody, int index) {
        return JsonPath.read(workoutBody, "$.exercises[" + index + "].id");
    }

    protected ResultActions putSet(String username, int workoutId, UUID clientId, String json) throws Exception {
        return mockMvc.perform(put("/backend/workouts/" + workoutId + "/sets/" + clientId)
                .header("Authorization", tokenFor(username))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    /** Logs a working set of reps and weight and returns its client id. */
    protected UUID logSet(String username, int workoutId, int entryId, int setNumber, int reps, double weightKg)
            throws Exception {
        UUID clientId = UUID.randomUUID();
        putSet(username, workoutId, clientId, setJson(entryId, setNumber, reps, weightKg))
                .andExpect(status().isOk());
        flushAndClear();
        return clientId;
    }

    protected String setJson(int entryId, int setNumber, int reps, double weightKg) {
        return """
                {"workoutExerciseId": %d, "setNumber": %d, "reps": %d, "weightKg": %s}
                """.formatted(entryId, setNumber, reps, weightKg);
    }

    protected void finishWorkout(String username, int workoutId) throws Exception {
        mockMvc.perform(post("/backend/workouts/" + workoutId + "/finish")
                        .header("Authorization", tokenFor(username)))
                .andExpect(status().isNoContent());
        flushAndClear();
    }

    /** Moves the start of a workout into the past, e.g. "5 hours". */
    protected void ageWorkout(int workoutId, String interval) {
        jdbcTemplate.update("UPDATE workout SET started_at = now() - CAST(? AS interval) WHERE id = ?",
                interval, workoutId);
    }

    /** Moves the creation of all sets of a workout into the past. */
    protected void ageSets(int workoutId, String interval) {
        jdbcTemplate.update("""
                UPDATE workout_set SET created_at = now() - CAST(? AS interval)
                WHERE workout_exercise_id IN (SELECT id FROM workout_exercise WHERE workout_id = ?)
                """, interval, workoutId);
    }

    protected int setCount(int workoutId) {
        return jdbcTemplate.queryForObject("""
                SELECT count(*) FROM workout_set s
                JOIN workout_exercise we ON we.id = s.workout_exercise_id
                WHERE we.workout_id = ?
                """, Integer.class, workoutId);
    }
}
