package de.phil.fitness.backend.exercise;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;

import de.phil.fitness.backend.TestDatabase;
import de.phil.fitness.backend.auth.JwtService;
import de.phil.fitness.backend.exercise.model.Exercise;
import de.phil.fitness.backend.exercise.repository.ExerciseRepository;
import de.phil.fitness.backend.user.repository.UserRepository;

/**
 * The whole access rule for exercises is one clause in ExerciseRepository:
 * {@code ownerUserId IS NULL OR ownerUserId = :userId}. If it is ever dropped, nothing fails -
 * users simply start seeing each other's definitions. These tests are what makes that loud.
 *
 * <p>Ids are looked up rather than hardcoded, so a change in seeding order does not turn into a
 * confusing test failure.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"dev", "test"})
@Import(TestDatabase.class)
class ExerciseAccessTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    private String tokenFor(String username) {
        Long id = userRepository.findByUsername(username).orElseThrow().getId();
        return jwtService.generateAccessToken(id.toString());
    }

    /** Ids of the exercises the given user can see, taken from the list endpoint itself. */
    private List<Integer> visibleIds(String username) throws Exception {
        String body = mockMvc.perform(get("/backend/exercises")
                        .header("Authorization", "Bearer " + tokenFor(username)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(body, "$[*].id");
    }

    private long idOfOwnedExercise(String username) {
        Long ownerId = userRepository.findByUsername(username).orElseThrow().getId();
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

    private long idOfGlobalExercise() {
        return exerciseRepository.findAll().stream()
                .filter(e -> e.getOwnerUserId() == null)
                .map(Exercise::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("seed has no global exercise"));
    }

    @Test
    @DisplayName("a global exercise is readable by anyone")
    void globalExerciseIsVisible() throws Exception {
        mockMvc.perform(get("/backend/exercises/" + idOfGlobalExercise())
                        .header("Authorization", "Bearer " + tokenFor("max")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.custom").value(false));
    }

    @Test
    @DisplayName("an own exercise is readable by its owner")
    void ownExerciseIsVisible() throws Exception {
        mockMvc.perform(get("/backend/exercises/" + idOfOwnedExercise("max"))
                        .header("Authorization", "Bearer " + tokenFor("max")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.custom").value(true));
    }

    @Test
    @DisplayName("someone else's exercise is indistinguishable from a missing one")
    void foreignExerciseIsNotFound() throws Exception {
        // 404 rather than 403 on purpose: a 403 would confirm that the id exists.
        mockMvc.perform(get("/backend/exercises/" + idOfOwnedExercise("lena_lifts"))
                        .header("Authorization", "Bearer " + tokenFor("max")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EXERCISE_NOT_FOUND"));
    }

    @Test
    @DisplayName("a soft deleted exercise is gone even for its owner")
    void softDeletedExerciseIsNotFound() throws Exception {
        mockMvc.perform(get("/backend/exercises/" + idOfSoftDeletedExercise())
                        .header("Authorization", "Bearer " + tokenFor("sina")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("EXERCISE_NOT_FOUND"));
    }

    @Test
    @DisplayName("the list holds own exercises but no foreign ones")
    void listIsScopedToTheCaller() throws Exception {
        int ownId = (int) idOfOwnedExercise("max");
        int foreignId = (int) idOfOwnedExercise("lena_lifts");

        assertThat(visibleIds("max"))
                .contains(ownId)
                .doesNotContain(foreignId);

        assertThat(visibleIds("lena_lifts"))
                .contains(foreignId)
                .doesNotContain(ownId);
    }

    @Test
    @DisplayName("no soft deleted exercise ever appears in the list")
    void listExcludesSoftDeleted() throws Exception {
        assertThat(visibleIds("sina")).doesNotContain((int) idOfSoftDeletedExercise());
    }
}
