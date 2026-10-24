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
import de.phil.fitness.backend.exercise.model.MuscleGroup;
import de.phil.fitness.backend.exercise.repository.ExerciseRepository;
import de.phil.fitness.backend.exercise.repository.MuscleGroupRepository;
import de.phil.fitness.backend.user.repository.UserRepository;

/**
 * The muscle group filter in the frontend matches {@code groupId} of an exercise's muscles against the group ids of
 * the lookup. These tests pin both sides of that contract.
 *
 * <p>Ids are looked up by name rather than hardcoded, like in {@link ExerciseAccessTests}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"dev", "test"})
@Import(TestDatabase.class)
class MuscleGroupTests {

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

    private String tokenFor(String username) {
        Long id = userRepository.findByUsername(username).orElseThrow().getId();
        return jwtService.generateAccessToken(id.toString());
    }

    private String getAsMax(String path) throws Exception {
        return mockMvc.perform(get(path)
                        .header("Authorization", "Bearer " + tokenFor("max12")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    private int idOfExercise(String name) {
        return exerciseRepository.findAll().stream()
                .filter(e -> name.equals(e.getName()))
                .map(Exercise::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("seed has no exercise " + name))
                .intValue();
    }

    private int idOfMuscleGroup(String name) {
        return muscleGroupRepository.findAll().stream()
                .filter(m -> name.equals(m.getName()))
                .map(MuscleGroup::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("seed has no muscle group " + name))
                .intValue();
    }

    @Test
    @DisplayName("the muscle groups need a token")
    void muscleGroupsRequireToken() throws Exception {
        mockMvc.perform(get("/backend/muscle-groups"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
    }

    @Test
    @DisplayName("only groups are top level, each with its body region, areas sit below their group")
    void muscleGroupsAreATwoLevelTree() throws Exception {
        String body = getAsMax("/backend/muscle-groups");

        List<String> groupNames = JsonPath.read(body, "$[*].name");
        assertThat(groupNames)
                .contains("Chest", "Arms", "Legs")
                .doesNotContain("Upper Chest", "Triceps");

        List<String> bodyRegions = JsonPath.read(body, "$[*].bodyRegion");
        assertThat(bodyRegions).doesNotContainNull();

        List<String> chestAreas = JsonPath.read(body, "$[?(@.name == 'Chest')].areas[*].name");
        assertThat(chestAreas).containsExactly("Upper Chest");
    }

    @Test
    @DisplayName("an exercise lists its primary muscle first, an area points to its group")
    void exerciseMusclesCarryTheirGroup() throws Exception {
        String body = getAsMax("/backend/exercises/" + idOfExercise("Barbell Bench Press"));

        List<String> roles = JsonPath.read(body, "$.muscles[*].role");
        assertThat(roles).first().isEqualTo("PRIMARY");
        assertThat(roles).isSorted();

        List<Integer> chestGroupIds = JsonPath.read(body, "$.muscles[?(@.name == 'Chest')].groupId");
        assertThat(chestGroupIds).containsExactly(idOfMuscleGroup("Chest"));

        List<Integer> tricepsGroupIds = JsonPath.read(body, "$.muscles[?(@.name == 'Triceps')].groupId");
        assertThat(tricepsGroupIds).containsExactly(idOfMuscleGroup("Arms"));
    }

    @Test
    @DisplayName("the list carries the muscles of global and own exercises")
    void listCarriesMuscles() throws Exception {
        String body = getAsMax("/backend/exercises");

        List<String> benchPressMuscles = JsonPath.read(body,
                "$[?(@.name == 'Barbell Bench Press')].muscles[*].name");
        assertThat(benchPressMuscles).containsExactly("Chest", "Triceps", "Front Delts");

        List<String> ownExerciseMuscles = JsonPath.read(body,
                "$[?(@.name == 'Slow Tempo Push-Up')].muscles[*].name");
        assertThat(ownExerciseMuscles).containsExactly("Chest", "Triceps");
    }
}
