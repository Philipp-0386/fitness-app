package de.phil.fitness.backend.exercise;

import com.jayway.jsonpath.JsonPath;
import de.phil.fitness.backend.TestDatabase;
import de.phil.fitness.backend.auth.JwtService;
import de.phil.fitness.backend.exercise.model.Exercise;
import de.phil.fitness.backend.exercise.model.MuscleGroup;
import de.phil.fitness.backend.exercise.repository.ExerciseRepository;
import de.phil.fitness.backend.exercise.repository.MuscleGroupRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Create and update of own exercises: who may change what, when the tracking type is locked, and how the muscles
 * of an exercise are set.
 *
 * <p>Every test creates the exercise it works on and runs in a transaction that is rolled back
 * afterwards. Exercise id 1 is the global Barbell Bench Press from the V2 reference data.
 *
 * <p>All requests of a test share one persistence context, see {@code RoutineWriteTests}. Tests call
 * {@link #flushAndClear()} before an update and before a read, so both hit the database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"dev", "test"})
@Import(TestDatabase.class)
class ExerciseWriteTests {

    private static final String UPDATE_BODY = """
            {"name": "Updated Curl", "exerciseType": "STRENGTH", "trackingType": "WEIGHT_REPS"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private MuscleGroupRepository muscleGroupRepository;

    @Autowired
    private EntityManager entityManager;

    private String tokenFor(String username) {
        return "Bearer " + jwtService.generateAccessToken(userId(username).toString());
    }

    private Long userId(String username) {
        return userRepository.findByUsername(username).orElseThrow().getId();
    }

    /** Writes pending changes to the database and forgets all loaded entities, so the next read hits the database. */
    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private int idOfMuscleGroup(String name) {
        return muscleGroupRepository.findAll().stream()
                .filter(m -> name.equals(m.getName()))
                .map(MuscleGroup::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("seed has no muscle group " + name))
                .intValue();
    }

    /** Creates a WEIGHT_REPS exercise for the given user and returns its id. */
    private Integer createExercise(String username, String name) throws Exception {
        String body = mockMvc.perform(post("/backend/exercises")
                        .header("Authorization", tokenFor(username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"name": "%s", "exerciseType": "STRENGTH", "trackingType": "WEIGHT_REPS"}
                        """.formatted(name)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.id");
    }

    // -----------------------------------------------------------------------
    // Create
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("an own exercise can be created, the Location header points to it")
    void createReturnsLocation() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/backend/exercises")
                .header("Authorization", tokenFor("max12"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "Test Curl", "exerciseType": "STRENGTH", "trackingType": "WEIGHT_REPS"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.custom").value(true))
                .andReturn().getResponse();
        Integer id = JsonPath.read(response.getContentAsString(), "$.id");

        assertThat(response.getHeader("Location")).endsWith("/backend/exercises/" + id);
    }

    // -----------------------------------------------------------------------
    // Update
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("an update replaces every field, an omitted description is cleared")
    void updateReplacesFields() throws Exception {
        String created = mockMvc.perform(post("/backend/exercises")
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"name": "Plank Hold", "exerciseType": "STRENGTH", "trackingType": "WEIGHT_REPS",
                         "description": "to be cleared"}
                        """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(created, "$.id");
        flushAndClear();

        mockMvc.perform(put("/backend/exercises/" + id)
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"name": "Side Plank", "exerciseType": "MOBILITY", "trackingType": "DURATION"}
                        """))
                .andExpect(status().isOk());
        flushAndClear();

        mockMvc.perform(get("/backend/exercises/" + id)
                        .header("Authorization", tokenFor("max12")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Side Plank"))
                .andExpect(jsonPath("$.exerciseType").value("MOBILITY"))
                .andExpect(jsonPath("$.trackingType").value("DURATION"))
                .andExpect(jsonPath("$.description").value(nullValue()))
                .andExpect(jsonPath("$.custom").value(true));
    }

    @Test
    @Transactional
    @DisplayName("a global exercise can not be changed and stays as it was")
    void globalExerciseIsForbidden() throws Exception {
        mockMvc.perform(put("/backend/exercises/1")
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_BODY))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("GLOBAL_EXERCISE_NOT_EDITABLE"));

        // Guards the order in the service: a change applied before the check would be flushed here.
        flushAndClear();

        mockMvc.perform(get("/backend/exercises/1")
                        .header("Authorization", tokenFor("max12")))
                .andExpect(jsonPath("$.name").value("Barbell Bench Press"));
    }

    @Test
    @Transactional
    @DisplayName("someone else's exercise is indistinguishable from a missing one")
    void foreignExerciseIsNotFound() throws Exception {
        Integer id = createExercise("lena_lifts", "Lena Curl");
        flushAndClear();

        mockMvc.perform(put("/backend/exercises/" + id)
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EXERCISE_NOT_FOUND"));
    }

    @Test
    @Transactional
    @DisplayName("a soft deleted exercise can not be changed, even by its owner")
    void softDeletedExerciseIsNotFound() throws Exception {
        Long id = exerciseRepository.findAll().stream()
                .filter(e -> e.getDeletedAt() != null)
                .map(Exercise::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("seed has no soft deleted exercise"));

        mockMvc.perform(put("/backend/exercises/" + id)
                        .header("Authorization", tokenFor("sina1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EXERCISE_NOT_FOUND"));
    }

    // -----------------------------------------------------------------------
    // Tracking type lock
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("the tracking type can change while no routine references the exercise")
    void trackingTypeChangesWhileUnreferenced() throws Exception {
        Integer id = createExercise("max12", "Free Curl");
        flushAndClear();

        mockMvc.perform(put("/backend/exercises/" + id)
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"name": "Free Curl", "exerciseType": "STRENGTH", "trackingType": "DURATION"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingType").value("DURATION"));
    }

    @Test
    @Transactional
    @DisplayName("the tracking type is locked once a routine references the exercise, other fields are not")
    void trackingTypeLockedWhileReferenced() throws Exception {
        Integer id = createExercise("max12", "Locked Curl");
        mockMvc.perform(post("/backend/routines")
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"name": "Arms", "exercises": [{"exerciseId": %d, "targetSets": 3}]}
                        """.formatted(id)))
                .andExpect(status().isCreated());
        // isReferenced is a native query, the routine has to be in the database before it runs.
        flushAndClear();

        mockMvc.perform(put("/backend/exercises/" + id)
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"name": "Locked Curl", "exerciseType": "STRENGTH", "trackingType": "DURATION"}
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("TRACKING_TYPE_LOCKED"));

        mockMvc.perform(put("/backend/exercises/" + id)
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"name": "Renamed Curl", "exerciseType": "STRENGTH", "trackingType": "WEIGHT_REPS"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Renamed Curl"));
    }

    // -----------------------------------------------------------------------
    // Muscles
    // -----------------------------------------------------------------------

    /** Creates an exercise for max12 that trains Quadriceps primary and Hamstrings secondary, returns its id. */
    private Integer createLegExercise() throws Exception {
        String body = mockMvc.perform(post("/backend/exercises")
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"name": "Split Squat", "exerciseType": "STRENGTH", "trackingType": "WEIGHT_REPS",
                         "muscles": [{"muscleGroupId": %d, "role": "PRIMARY"}, {"muscleGroupId": %d, "role": "SECONDARY"}]}
                        """.formatted(idOfMuscleGroup("Quadriceps"), idOfMuscleGroup("Hamstrings"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.id");
    }

    private List<String> muscleNames(Integer exerciseId, String role) throws Exception {
        String body = mockMvc.perform(get("/backend/exercises/" + exerciseId)
                        .header("Authorization", tokenFor("max12")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.muscles[?(@.role == '" + role + "')].name");
    }

    @Test
    @Transactional
    @DisplayName("muscles sent on create are stored with their role")
    void createStoresMuscles() throws Exception {
        Integer id = createLegExercise();
        flushAndClear();

        assertThat(muscleNames(id, "PRIMARY")).containsExactly("Quadriceps");
        assertThat(muscleNames(id, "SECONDARY")).containsExactly("Hamstrings");
    }

    @Test
    @Transactional
    @DisplayName("an update that sends the same muscles again succeeds and keeps them")
    void updateWithUnchangedMuscles() throws Exception {
        Integer id = createLegExercise();
        flushAndClear();

        mockMvc.perform(put("/backend/exercises/" + id)
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"name": "Split Squat", "exerciseType": "STRENGTH", "trackingType": "WEIGHT_REPS",
                         "muscles": [{"muscleGroupId": %d, "role": "PRIMARY"}, {"muscleGroupId": %d, "role": "SECONDARY"}]}
                        """.formatted(idOfMuscleGroup("Quadriceps"), idOfMuscleGroup("Hamstrings"))))
                .andExpect(status().isOk());
        flushAndClear();

        assertThat(muscleNames(id, "PRIMARY")).containsExactly("Quadriceps");
        assertThat(muscleNames(id, "SECONDARY")).containsExactly("Hamstrings");
    }

    @Test
    @Transactional
    @DisplayName("an update replaces the muscles, including the role of a muscle that stays")
    void updateReplacesMuscles() throws Exception {
        Integer id = createLegExercise();
        flushAndClear();

        mockMvc.perform(put("/backend/exercises/" + id)
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"name": "Split Squat", "exerciseType": "STRENGTH", "trackingType": "WEIGHT_REPS",
                         "muscles": [{"muscleGroupId": %d, "role": "PRIMARY"}, {"muscleGroupId": %d, "role": "SECONDARY"}]}
                        """.formatted(idOfMuscleGroup("Glutes"), idOfMuscleGroup("Quadriceps"))))
                .andExpect(status().isOk());
        flushAndClear();

        assertThat(muscleNames(id, "PRIMARY")).containsExactly("Glutes");
        assertThat(muscleNames(id, "SECONDARY")).containsExactly("Quadriceps");
    }

    @Test
    @Transactional
    @DisplayName("an update without muscles clears them")
    void updateWithoutMusclesClearsThem() throws Exception {
        Integer id = createLegExercise();
        flushAndClear();

        mockMvc.perform(put("/backend/exercises/" + id)
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UPDATE_BODY))
                .andExpect(status().isOk());
        flushAndClear();

        mockMvc.perform(get("/backend/exercises/" + id)
                        .header("Authorization", tokenFor("max12")))
                .andExpect(jsonPath("$.muscles").isEmpty());
    }

    @Test
    @Transactional
    @DisplayName("the same muscle twice is rejected, even with different roles")
    void duplicateMuscleIsRejected() throws Exception {
        int quadriceps = idOfMuscleGroup("Quadriceps");

        mockMvc.perform(post("/backend/exercises")
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"name": "Double Squat", "exerciseType": "STRENGTH", "trackingType": "WEIGHT_REPS",
                         "muscles": [{"muscleGroupId": %d, "role": "PRIMARY"}, {"muscleGroupId": %d, "role": "SECONDARY"}]}
                        """.formatted(quadriceps, quadriceps)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.muscleIdsUnique").exists());
    }

    @Test
    @Transactional
    @DisplayName("an unknown muscle group is rejected as a field error")
    void unknownMuscleGroupIsRejected() throws Exception {
        mockMvc.perform(post("/backend/exercises")
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"name": "Ghost Squat", "exerciseType": "STRENGTH", "trackingType": "WEIGHT_REPS",
                         "muscles": [{"muscleGroupId": 999999, "role": "PRIMARY"}]}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.muscles").exists());
    }
}
