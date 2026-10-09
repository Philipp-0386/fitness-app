package de.phil.fitness.backend.routine;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

import de.phil.fitness.backend.TestDatabase;
import de.phil.fitness.backend.auth.JwtService;
import de.phil.fitness.backend.exercise.model.Exercise;
import de.phil.fitness.backend.exercise.repository.ExerciseRepository;
import de.phil.fitness.backend.routine.model.Routine;
import de.phil.fitness.backend.routine.repository.RoutineRepository;
import de.phil.fitness.backend.user.repository.UserRepository;

/**
 * Ownership of routines lives in the WHERE clauses of RoutineRepository, and the availability of
 * referenced exercises in one service call. Dropping either fails silently: users would read or
 * change each other's routines, or leak foreign exercise names through their own.
 *
 * Every rejection must look exactly like a missing id, otherwise a caller could probe which
 * ids exist. Tests that send writes run in a rolled back transaction, so a regression cannot
 * leave data behind for other tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"dev", "test"})
@Import(TestDatabase.class)
class RoutineAccessTests {

    private static final long UNKNOWN_ID = 999999;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    private Long userId(String username) {
        return userRepository.findByUsername(username).orElseThrow().getId();
    }

    private String tokenFor(String username) {
        return "Bearer " + jwtService.generateAccessToken(userId(username).toString());
    }

    private long idOfOwnedRoutine(String username) {
        return routineRepository.getAllUserRoutinesByUserId(userId(username)).stream()
                .map(Routine::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("seed has no routine for " + username));
    }

    private long idOfPresetRoutine() {
        return routineRepository.getAllPresetRoutines().stream()
                .map(Routine::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("seed has no preset routine"));
    }

    private long idOfOwnedExercise(String username) {
        Long ownerId = userId(username);
        return exerciseRepository.findAvailableTo(ownerId).stream()
                .filter(e -> ownerId.equals(e.getOwnerUserId()))
                .map(Exercise::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("seed has no own exercise for " + username));
    }

    private long idOfSoftDeletedExercise() {
        return exerciseRepository.findAll().stream()
                .filter(e -> e.getDeletedAt() != null)
                .map(Exercise::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("seed has no soft deleted exercise"));
    }

    private String detailBody(String username, long routineId) throws Exception {
        return mockMvc.perform(get("/backend/routines/" + routineId)
                        .header("Authorization", tokenFor(username)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    private String routineWithExercise(long exerciseId) {
        return """
                {"name": "Probe", "exercises": [{"exerciseId": %d, "targetSets": 3}]}
                """.formatted(exerciseId);
    }

    // -----------------------------------------------------------------------
    // Own routines
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("the list holds own routines but no foreign ones and no presets")
    void listIsScopedToTheCaller() throws Exception {
        String body = mockMvc.perform(get("/backend/routines")
                        .header("Authorization", tokenFor("max")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Integer> ids = JsonPath.read(body, "$[*].id");

        assertThat(ids)
                .contains((int) idOfOwnedRoutine("max"))
                .doesNotContain((int) idOfOwnedRoutine("lena_lifts"), (int) idOfPresetRoutine());
    }

    @Test
    @DisplayName("someone else's routine is indistinguishable from a missing one")
    void foreignRoutineIsNotFound() throws Exception {
        String foreign = mockMvc.perform(get("/backend/routines/" + idOfOwnedRoutine("lena_lifts"))
                        .header("Authorization", tokenFor("max")))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        String missing = mockMvc.perform(get("/backend/routines/" + UNKNOWN_ID)
                        .header("Authorization", tokenFor("max")))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        assertThat((String) JsonPath.read(foreign, "$.code")).isEqualTo("USER_ROUTINE_NOT_FOUND");
        assertThat((String) JsonPath.read(foreign, "$.message")).isEqualTo(JsonPath.read(missing, "$.message"));
    }

    @Test
    @Transactional
    @DisplayName("someone else's routine cannot be updated")
    void foreignRoutineCannotBeUpdated() throws Exception {
        long lenasRoutine = idOfOwnedRoutine("lena_lifts");
        String before = detailBody("lena_lifts", lenasRoutine);

        mockMvc.perform(put("/backend/routines/" + lenasRoutine)
                        .header("Authorization", tokenFor("max"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Hijacked"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_ROUTINE_NOT_FOUND"));

        assertThat(detailBody("lena_lifts", lenasRoutine)).isEqualTo(before);
    }

    @Test
    @Transactional
    @DisplayName("someone else's routine cannot be deleted")
    void foreignRoutineCannotBeDeleted() throws Exception {
        long lenasRoutine = idOfOwnedRoutine("lena_lifts");

        mockMvc.perform(delete("/backend/routines/" + lenasRoutine)
                        .header("Authorization", tokenFor("max")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_ROUTINE_NOT_FOUND"));

        assertThat(routineRepository.findById(lenasRoutine)).isPresent();
    }

    // -----------------------------------------------------------------------
    // Presets and own routines stay on their own paths
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("a preset is not readable through the user path")
    void presetIsNotAnOwnRoutine() throws Exception {
        mockMvc.perform(get("/backend/routines/" + idOfPresetRoutine())
                        .header("Authorization", tokenFor("max")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_ROUTINE_NOT_FOUND"));
    }

    @Test
    @Transactional
    @DisplayName("a preset can be neither updated nor deleted")
    void presetCannotBeChanged() throws Exception {
        long preset = idOfPresetRoutine();
        String before = mockMvc.perform(get("/backend/routines/presets/" + preset)
                        .header("Authorization", tokenFor("max")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(put("/backend/routines/" + preset)
                        .header("Authorization", tokenFor("max"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Hijacked Preset"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_ROUTINE_NOT_FOUND"));
        mockMvc.perform(delete("/backend/routines/" + preset)
                        .header("Authorization", tokenFor("max")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_ROUTINE_NOT_FOUND"));

        String after = mockMvc.perform(get("/backend/routines/presets/" + preset)
                        .header("Authorization", tokenFor("max")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(after).isEqualTo(before);
    }

    @Test
    @DisplayName("an own routine is not readable through the preset path")
    void ownRoutineIsNotAPreset() throws Exception {
        mockMvc.perform(get("/backend/routines/presets/" + idOfOwnedRoutine("max"))
                        .header("Authorization", tokenFor("max")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PRESET_ROUTINE_NOT_FOUND"));
    }

    // -----------------------------------------------------------------------
    // Referenced exercises must be available to the caller
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("a routine cannot be created with someone else's exercise")
    void createWithForeignExerciseIsRefused() throws Exception {
        int before = routineRepository.getAllUserRoutinesByUserId(userId("max")).size();

        mockMvc.perform(post("/backend/routines")
                        .header("Authorization", tokenFor("max"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(routineWithExercise(idOfOwnedExercise("lena_lifts"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EXERCISE_NOT_FOUND"));

        assertThat(routineRepository.getAllUserRoutinesByUserId(userId("max"))).hasSize(before);
    }

    @Test
    @Transactional
    @DisplayName("a routine cannot be created with an own but soft deleted exercise")
    void createWithSoftDeletedExerciseIsRefused() throws Exception {
        mockMvc.perform(post("/backend/routines")
                        .header("Authorization", tokenFor("sina"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(routineWithExercise(idOfSoftDeletedExercise())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EXERCISE_NOT_FOUND"));

        assertThat(routineRepository.getAllUserRoutinesByUserId(userId("sina"))).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("an unknown exercise is refused by the service, not by the foreign key")
    void createWithUnknownExerciseIsNotFound() throws Exception {
        // Without the service check this would surface as 409 DATA_CONFLICT.
        mockMvc.perform(post("/backend/routines")
                        .header("Authorization", tokenFor("max"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(routineWithExercise(UNKNOWN_ID)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EXERCISE_NOT_FOUND"));
    }

    @Test
    @Transactional
    @DisplayName("an own routine cannot be updated with someone else's exercise")
    void updateWithForeignExerciseIsRefused() throws Exception {
        long ownRoutine = idOfOwnedRoutine("max");
        String before = detailBody("max", ownRoutine);

        mockMvc.perform(put("/backend/routines/" + ownRoutine)
                        .header("Authorization", tokenFor("max"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(routineWithExercise(idOfOwnedExercise("lena_lifts"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EXERCISE_NOT_FOUND"));

        assertThat(detailBody("max", ownRoutine)).isEqualTo(before);
    }

    @Test
    @Transactional
    @DisplayName("an update with an unknown exercise is refused before anything changes")
    void updateWithUnknownExerciseIsNotFound() throws Exception {
        long ownRoutine = idOfOwnedRoutine("max");
        String before = detailBody("max", ownRoutine);

        mockMvc.perform(put("/backend/routines/" + ownRoutine)
                        .header("Authorization", tokenFor("max"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(routineWithExercise(UNKNOWN_ID)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EXERCISE_NOT_FOUND"));

        assertThat(detailBody("max", ownRoutine)).isEqualTo(before);
    }
}
