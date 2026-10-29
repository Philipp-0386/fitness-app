package de.phil.fitness.backend.program;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

import de.phil.fitness.backend.TestDatabase;
import de.phil.fitness.backend.auth.JwtService;
import de.phil.fitness.backend.program.model.Program;
import de.phil.fitness.backend.program.repository.ProgramRepository;
import de.phil.fitness.backend.routine.model.Routine;
import de.phil.fitness.backend.routine.repository.RoutineRepository;
import de.phil.fitness.backend.user.repository.UserRepository;

/**
 * Ownership of programs lives in the WHERE clauses of ProgramRepository, and the ownership of
 * referenced routines in one service call. Dropping either fails silently: users would read each
 * other's programs, or build a program on someone else's routine and see its exercises.
 *
 * Every rejection must look exactly like a missing id, otherwise a caller could probe which
 * ids exist. Tests that send writes run in a rolled back transaction, so a regression cannot
 * leave data behind for other tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"dev", "test"})
@Import(TestDatabase.class)
class ProgramAccessTests {

    private static final long UNKNOWN_ID = 999999;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private RoutineRepository routineRepository;

    private Long userId(String username) {
        return userRepository.findByUsername(username).orElseThrow().getId();
    }

    private String tokenFor(String username) {
        return "Bearer " + jwtService.generateAccessToken(userId(username).toString());
    }

    private long idOfOwnedProgram(String username) {
        return programRepository.getAllUserProgramsByUserId(userId(username)).stream()
                .map(Program::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("seed has no program for " + username));
    }

    private long idOfPresetProgram() {
        return programRepository.getAllPresetPrograms().stream()
                .map(Program::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("seed has no preset program"));
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

    private String programWithRoutine(long routineId) {
        return """
                {"name": "Probe", "days": [{"routineId": %d}, {"routineId": null}]}
                """.formatted(routineId);
    }

    // -----------------------------------------------------------------------
    // Own programs
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("the list holds own programs once each, but no foreign ones and no presets")
    void listIsScopedToTheCaller() throws Exception {
        String body = mockMvc.perform(get("/backend/programs")
                        .header("Authorization", tokenFor("max12")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Integer> ids = JsonPath.read(body, "$[*].id");

        // The query fetches the days with a join, a missing deduplication would repeat a program per day.
        assertThat(ids)
                .doesNotHaveDuplicates()
                .hasSize(programRepository.getAllUserProgramsByUserId(userId("max12")).size())
                .contains((int) idOfOwnedProgram("max12"))
                .doesNotContain((int) idOfOwnedProgram("lena_lifts"), (int) idOfPresetProgram());
    }

    @Test
    @DisplayName("someone else's program is indistinguishable from a missing one")
    void foreignProgramIsNotFound() throws Exception {
        String foreign = mockMvc.perform(get("/backend/programs/" + idOfOwnedProgram("lena_lifts"))
                        .header("Authorization", tokenFor("max12")))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        String missing = mockMvc.perform(get("/backend/programs/" + UNKNOWN_ID)
                        .header("Authorization", tokenFor("max12")))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        assertThat((String) JsonPath.read(foreign, "$.code")).isEqualTo("USER_PROGRAM_NOT_FOUND");
        assertThat((String) JsonPath.read(foreign, "$.message")).isEqualTo(JsonPath.read(missing, "$.message"));
    }

    @Test
    @DisplayName("the active program is the caller's own")
    void activeProgramIsTheCallersOwn() throws Exception {
        mockMvc.perform(get("/backend/programs/active")
                        .header("Authorization", tokenFor("lena_lifts")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Legs and rest"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("without an active program the answer is 204 without a body")
    void noActiveProgramIsNoContent() throws Exception {
        mockMvc.perform(get("/backend/programs/active")
                        .header("Authorization", tokenFor("sina1")))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    @Transactional
    @DisplayName("someone else's program cannot be deleted")
    void foreignProgramCannotBeDeleted() throws Exception {
        long lenasProgram = idOfOwnedProgram("lena_lifts");

        mockMvc.perform(delete("/backend/programs/" + lenasProgram)
                        .header("Authorization", tokenFor("max12")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_PROGRAM_NOT_FOUND"));

        assertThat(programRepository.findById(lenasProgram)).isPresent();
    }

    @Test
    @Transactional
    @DisplayName("someone else's program cannot be updated")
    void foreignProgramCannotBeUpdated() throws Exception {
        long lenasProgram = idOfOwnedProgram("lena_lifts");
        String nameBefore = programRepository.findById(lenasProgram).orElseThrow().getName();

        mockMvc.perform(put("/backend/programs/" + lenasProgram)
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(programWithRoutine(idOfOwnedRoutine("max12"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_PROGRAM_NOT_FOUND"));

        assertThat(programRepository.findById(lenasProgram).orElseThrow().getName()).isEqualTo(nameBefore);
    }

    @Test
    @Transactional
    @DisplayName("someone else's program cannot be activated, the own active program stays active")
    void foreignProgramCannotBeActivated() throws Exception {
        mockMvc.perform(put("/backend/programs/active")
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"programId": %d}
                                """.formatted(idOfOwnedProgram("lena_lifts"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_PROGRAM_NOT_FOUND"));

        mockMvc.perform(get("/backend/programs/active")
                        .header("Authorization", tokenFor("max12")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Push Pull"));
    }

    // -----------------------------------------------------------------------
    // Days must belong to the updated program
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("a day of another program, own or foreign, cannot be moved into a program")
    void dayOfAnotherProgramIsRefused() throws Exception {
        List<Program> maxPrograms = programRepository.getAllUserProgramsByUserId(userId("max12"));
        long target = maxPrograms.get(0).getId();
        long ownOtherDay = maxPrograms.get(1).getDays().get(0).getId();
        long foreignDay = programRepository.getAllUserProgramsByUserId(userId("lena_lifts"))
                .get(0).getDays().get(0).getId();
        long routine = idOfOwnedRoutine("max12");

        for (long day : List.of(ownOtherDay, foreignDay)) {
            mockMvc.perform(put("/backend/programs/" + target)
                            .header("Authorization", tokenFor("max12"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name": "Probe", "days": [{"id": %d, "routineId": %d}]}
                                    """.formatted(day, routine)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value("PROGRAM_DAY_NOT_FOUND"));
        }
    }

    // -----------------------------------------------------------------------
    // Presets and own programs stay on their own paths
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("the preset list holds presets only")
    void presetListHoldsNoOwnPrograms() throws Exception {
        String body = mockMvc.perform(get("/backend/programs/presets")
                        .header("Authorization", tokenFor("max12")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Integer> ids = JsonPath.read(body, "$[*].id");

        assertThat(ids)
                .contains((int) idOfPresetProgram())
                .doesNotContain((int) idOfOwnedProgram("max12"), (int) idOfOwnedProgram("lena_lifts"));
    }

    @Test
    @DisplayName("a preset is not readable through the user path")
    void presetIsNotAnOwnProgram() throws Exception {
        mockMvc.perform(get("/backend/programs/" + idOfPresetProgram())
                        .header("Authorization", tokenFor("max12")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_PROGRAM_NOT_FOUND"));
    }

    @Test
    @Transactional
    @DisplayName("a preset can be neither updated, activated nor deleted")
    void presetCannotBeChanged() throws Exception {
        long preset = idOfPresetProgram();
        String before = mockMvc.perform(get("/backend/programs/presets/" + preset)
                        .header("Authorization", tokenFor("max12")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(put("/backend/programs/" + preset)
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(programWithRoutine(idOfOwnedRoutine("max12"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_PROGRAM_NOT_FOUND"));
        mockMvc.perform(put("/backend/programs/active")
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"programId": %d}
                                """.formatted(preset)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_PROGRAM_NOT_FOUND"));
        mockMvc.perform(delete("/backend/programs/" + preset)
                        .header("Authorization", tokenFor("max12")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_PROGRAM_NOT_FOUND"));

        String after = mockMvc.perform(get("/backend/programs/presets/" + preset)
                        .header("Authorization", tokenFor("max12")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(after).isEqualTo(before);
    }

    @Test
    @DisplayName("an own program is not readable through the preset path")
    void ownProgramIsNotAPreset() throws Exception {
        mockMvc.perform(get("/backend/programs/presets/" + idOfOwnedProgram("max12"))
                        .header("Authorization", tokenFor("max12")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PRESET_PROGRAM_NOT_FOUND"));
    }

    // -----------------------------------------------------------------------
    // Referenced routines must be owned by the caller
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("a program cannot be created with someone else's routine")
    void createWithForeignRoutineIsRefused() throws Exception {
        int before = programRepository.getAllUserProgramsByUserId(userId("max12")).size();

        mockMvc.perform(post("/backend/programs")
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(programWithRoutine(idOfOwnedRoutine("lena_lifts"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_ROUTINE_NOT_FOUND"));

        assertThat(programRepository.getAllUserProgramsByUserId(userId("max12"))).hasSize(before);
    }

    @Test
    @Transactional
    @DisplayName("an own program cannot be updated with someone else's routine")
    void updateWithForeignRoutineIsRefused() throws Exception {
        long program = idOfOwnedProgram("max12");
        String nameBefore = programRepository.findById(program).orElseThrow().getName();

        mockMvc.perform(put("/backend/programs/" + program)
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(programWithRoutine(idOfOwnedRoutine("lena_lifts"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_ROUTINE_NOT_FOUND"));

        assertThat(programRepository.findById(program).orElseThrow().getName()).isEqualTo(nameBefore);
    }

    @Test
    @Transactional
    @DisplayName("a program cannot be created with a preset routine, presets are copied first")
    void createWithPresetRoutineIsRefused() throws Exception {
        mockMvc.perform(post("/backend/programs")
                        .header("Authorization", tokenFor("sina1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(programWithRoutine(idOfPresetRoutine())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_ROUTINE_NOT_FOUND"));

        assertThat(programRepository.getAllUserProgramsByUserId(userId("sina1"))).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("an unknown routine is refused by the service, not by the foreign key")
    void createWithUnknownRoutineIsNotFound() throws Exception {
        // Without the service check this would surface as 409 DATA_CONFLICT.
        mockMvc.perform(post("/backend/programs")
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(programWithRoutine(UNKNOWN_ID)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_ROUTINE_NOT_FOUND"));
    }
}
