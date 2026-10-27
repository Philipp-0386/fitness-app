package de.phil.fitness.backend.program;

import com.jayway.jsonpath.JsonPath;
import de.phil.fitness.backend.TestDatabase;
import de.phil.fitness.backend.auth.JwtService;
import de.phil.fitness.backend.program.repository.ProgramRepository;
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

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Create of own programs: the order of days, rest days, which program becomes active, the
 * validation of the body and the program limit.
 *
 * <p>Every test creates the routines it references, so no test depends on the seeded routines, and
 * runs in a transaction that is rolled back afterwards. Exercise ids are global exercises from the
 * V2 reference data: 1 Barbell Bench Press (WEIGHT_REPS), 38 Plank (DURATION). Seeded programs:
 * max12 owns three with "Push Pull" active, sina1 owns none.
 *
 * <p>Within that transaction all requests share one persistence context, unlike the real
 * application where every request gets its own. Tests therefore call {@link #flushAndClear()}
 * before a read, so it shows what was actually stored instead of the entity in memory.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"dev", "test"})
@Import(TestDatabase.class)
class ProgramWriteTests {

    /** Mirrors the limit checked in ProgramService.createNewUserProgram. */
    private static final int PROGRAM_LIMIT = 5;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private EntityManager entityManager;

    private Long userId(String username) {
        return userRepository.findByUsername(username).orElseThrow().getId();
    }

    private String tokenFor(String username) {
        return "Bearer " + jwtService.generateAccessToken(userId(username).toString());
    }

    /** Creates a routine with one exercise for the user and returns its id. */
    private int createRoutine(String username, String name, long exerciseId) throws Exception {
        String body = mockMvc.perform(post("/backend/routines")
                        .header("Authorization", tokenFor(username))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "%s", "exercises": [{"exerciseId": %d, "targetSets": 3}]}
                                """.formatted(name, exerciseId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.id");
    }

    /** Creates a program for the user and returns the response body. */
    private String createProgram(String username, String json) throws Exception {
        return mockMvc.perform(post("/backend/programs")
                        .header("Authorization", tokenFor(username))
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
    @DisplayName("days follow the list, a rest day has no routine, the Location header points to the program")
    void createOrdersDaysByListPosition() throws Exception {
        int bench = createRoutine("max12", "Bench Day", 1);
        int plank = createRoutine("max12", "Plank Day", 38);

        MockHttpServletResponse response = mockMvc.perform(post("/backend/programs")
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "Ordered",
                          "description": "Two days and a rest.",
                          "days": [
                            { "routineId": %d },
                            { "routineId": null },
                            { "routineId": %d }
                          ]
                        }
                        """.formatted(plank, bench)))
                .andExpect(status().isCreated())
                .andReturn().getResponse();
        Integer programId = JsonPath.read(response.getContentAsString(), "$.id");
        assertThat(response.getHeader("Location")).endsWith("/backend/programs/" + programId);
        flushAndClear();

        mockMvc.perform(get("/backend/programs/" + programId)
                        .header("Authorization", tokenFor("max12")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ordered"))
                .andExpect(jsonPath("$.description").value("Two days and a rest."))
                .andExpect(jsonPath("$.days.length()").value(3))
                .andExpect(jsonPath("$.days[0].orderIndex").value(0))
                .andExpect(jsonPath("$.days[0].routine.id").value(plank))
                .andExpect(jsonPath("$.days[0].routine.exerciseNames[0]").value("Plank"))
                .andExpect(jsonPath("$.days[1].orderIndex").value(1))
                .andExpect(jsonPath("$.days[1].routine").value(nullValue()))
                .andExpect(jsonPath("$.days[2].orderIndex").value(2))
                .andExpect(jsonPath("$.days[2].routine.id").value(bench))
                .andExpect(jsonPath("$.days[2].routine.exerciseNames[0]").value("Barbell Bench Press"));
    }

    @Test
    @Transactional
    @DisplayName("the same routine may fill several days")
    void createAllowsSameRoutineTwice() throws Exception {
        int bench = createRoutine("max12", "Bench Day", 1);

        String created = createProgram("max12", """
                {"name": "Bench Twice", "days": [{"routineId": %d}, {"routineId": null}, {"routineId": %d}]}
                """.formatted(bench, bench));

        assertThat((Integer) JsonPath.read(created, "$.days[0].routine.id")).isEqualTo(bench);
        assertThat((Integer) JsonPath.read(created, "$.days[2].routine.id")).isEqualTo(bench);
    }

    @Test
    @Transactional
    @DisplayName("the first program of a user becomes the active one")
    void firstProgramBecomesActive() throws Exception {
        int bench = createRoutine("sina1", "Bench Day", 1);

        String created = createProgram("sina1", """
                {"name": "First", "days": [{"routineId": %d}]}
                """.formatted(bench));
        Integer programId = JsonPath.read(created, "$.id");
        flushAndClear();

        assertThat((Boolean) JsonPath.read(created, "$.active")).isTrue();
        mockMvc.perform(get("/backend/programs/active")
                        .header("Authorization", tokenFor("sina1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(programId));
    }

    @Test
    @Transactional
    @DisplayName("a further program stays inactive and leaves the active one alone")
    void furtherProgramStaysInactive() throws Exception {
        int bench = createRoutine("max12", "Bench Day", 1);

        String created = createProgram("max12", """
                {"name": "Another", "days": [{"routineId": %d}]}
                """.formatted(bench));
        flushAndClear();

        assertThat((Boolean) JsonPath.read(created, "$.active")).isFalse();
        mockMvc.perform(get("/backend/programs/active")
                        .header("Authorization", tokenFor("max12")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Push Pull"));
    }

    // -----------------------------------------------------------------------
    // Validation
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("a program of rest days only is rejected")
    void createRejectsRestDaysOnly() throws Exception {
        mockMvc.perform(post("/backend/programs")
                        .header("Authorization", tokenFor("sina1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"name": "Lazy", "days": [{"routineId": null}, {"routineId": null}]}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.trainingDayPresent").exists());

        assertThat(programRepository.countByUserId(userId("sina1"))).isZero();
    }

    @Test
    @Transactional
    @DisplayName("missing or empty days and a blank name are reported per field")
    void createRejectsMissingDaysAndName() throws Exception {
        mockMvc.perform(post("/backend/programs")
                        .header("Authorization", tokenFor("sina1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"name": " "}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.days").exists());

        mockMvc.perform(post("/backend/programs")
                        .header("Authorization", tokenFor("sina1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"name": "Empty", "days": []}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.days").exists());
    }

    @Test
    @Transactional
    @DisplayName("more than 14 days are rejected")
    void createRejectsTooManyDays() throws Exception {
        int bench = createRoutine("sina1", "Bench Day", 1);
        String days = String.join(",", Collections.nCopies(15, "{\"routineId\": " + bench + "}"));

        mockMvc.perform(post("/backend/programs")
                        .header("Authorization", tokenFor("sina1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"name": "Too Long", "days": [%s]}
                        """.formatted(days)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.days").exists());
    }

    @Test
    @Transactional
    @DisplayName("a null entry in the days is rejected instead of failing while mapping")
    void createRejectsNullDay() throws Exception {
        int bench = createRoutine("sina1", "Bench Day", 1);

        mockMvc.perform(post("/backend/programs")
                        .header("Authorization", tokenFor("sina1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"name": "Hole", "days": [{"routineId": %d}, null]}
                        """.formatted(bench)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));

        assertThat(programRepository.countByUserId(userId("sina1"))).isZero();
    }

    // -----------------------------------------------------------------------
    // Limit
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("once the limit is reached a further program is refused")
    void createBeyondLimitIsRefused() throws Exception {
        int bench = createRoutine("max12", "Bench Day", 1);
        String program = """
                {"name": "Filler", "days": [{"routineId": %d}]}
                """.formatted(bench);
        for (long owned = programRepository.countByUserId(userId("max12")); owned < PROGRAM_LIMIT; owned++) {
            createProgram("max12", program);
        }

        mockMvc.perform(post("/backend/programs")
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(program))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PROGRAM_LIMIT_REACHED"));

        assertThat(programRepository.countByUserId(userId("max12"))).isEqualTo(PROGRAM_LIMIT);
    }
}
