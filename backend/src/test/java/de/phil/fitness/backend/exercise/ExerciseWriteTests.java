package de.phil.fitness.backend.exercise;

import com.jayway.jsonpath.JsonPath;
import de.phil.fitness.backend.TestDatabase;
import de.phil.fitness.backend.auth.JwtService;
import de.phil.fitness.backend.exercise.repository.ExerciseRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"dev", "test"})
@Import(TestDatabase.class)
public class ExerciseWriteTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

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

    private String createExercise(String json) throws Exception {
        return mockMvc.perform(post("/backend/exercises")
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    @Transactional
    @DisplayName("an owned exercise can be created")
    void createExercise() throws Exception {
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
}
