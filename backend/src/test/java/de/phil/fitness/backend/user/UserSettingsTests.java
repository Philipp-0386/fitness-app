package de.phil.fitness.backend.user;

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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Reading and replacing the own settings, their validation, and the settings row a sign-up creates.
 *
 * <p>Relies on the dev seed: max12 is metric, marco imperial, both on the default time zone.
 * Writing tests run in a transaction that is rolled back afterwards and call {@link #flushAndClear()}
 * before reading, so the read shows what was actually stored instead of the entity in memory.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"dev", "test"})
@Import(TestDatabase.class)
class UserSettingsTests {

    private static final String SETTINGS_PATH = "/backend/me/settings";

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
        return userRepository.findByUsernameIgnoreCase(username).orElseThrow().getId();
    }

    private String tokenFor(String username) {
        return "Bearer " + jwtService.generateAccessToken(userId(username).toString());
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    // -----------------------------------------------------------------------
    // Read Tests
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("the own settings are returned, not those of another user")
    void getReturnsOwnSettings() throws Exception {
        mockMvc.perform(get(SETTINGS_PATH).header("Authorization", tokenFor("marco")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weightUnit").value("LB"))
                .andExpect(jsonPath("$.distanceUnit").value("MI"))
                .andExpect(jsonPath("$.timeZone").value("Europe/Berlin"));

        mockMvc.perform(get(SETTINGS_PATH).header("Authorization", tokenFor("max12")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weightUnit").value("KG"))
                .andExpect(jsonPath("$.distanceUnit").value("KM"));
    }

    // -----------------------------------------------------------------------
    // Update Tests
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("an update replaces all settings and is stored")
    void updateReplacesAllSettings() throws Exception {
        mockMvc.perform(put(SETTINGS_PATH)
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"weightUnit": "LB", "distanceUnit": "MI", "timeZone": "America/New_York"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weightUnit").value("LB"))
                .andExpect(jsonPath("$.distanceUnit").value("MI"))
                .andExpect(jsonPath("$.timeZone").value("America/New_York"));

        flushAndClear();

        mockMvc.perform(get(SETTINGS_PATH).header("Authorization", tokenFor("max12")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.weightUnit").value("LB"))
                .andExpect(jsonPath("$.distanceUnit").value("MI"))
                .andExpect(jsonPath("$.timeZone").value("America/New_York"));
    }

    @Test
    @Transactional
    @DisplayName("an unknown time zone is refused as a field error")
    void unknownTimeZoneIsRefused() throws Exception {
        mockMvc.perform(put(SETTINGS_PATH)
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"weightUnit": "KG", "distanceUnit": "KM", "timeZone": "Mars/Base"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.timeZone").value("Unknown time zone"));
    }

    @Test
    @Transactional
    @DisplayName("a fixed offset is not accepted as time zone")
    void fixedOffsetIsRefused() throws Exception {
        mockMvc.perform(put(SETTINGS_PATH)
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"weightUnit": "KG", "distanceUnit": "KM", "timeZone": "+01:00"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.timeZone").value("Unknown time zone"));
    }

    @Test
    @Transactional
    @DisplayName("an unknown unit is refused with the allowed values")
    void unknownUnitIsRefused() throws Exception {
        mockMvc.perform(put(SETTINGS_PATH)
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"weightUnit": "STONE", "distanceUnit": "KM", "timeZone": "Europe/Berlin"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.weightUnit").value("must be one of: KG, LB"));
    }

    @Test
    @Transactional
    @DisplayName("missing fields are reported per field, the time zone only as required")
    void missingFieldsAreReported() throws Exception {
        mockMvc.perform(put(SETTINGS_PATH)
                        .header("Authorization", tokenFor("max12"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.weightUnit").value("Weight unit is required"))
                .andExpect(jsonPath("$.fieldErrors.distanceUnit").value("Distance unit is required"))
                .andExpect(jsonPath("$.fieldErrors.timeZone").value("Time zone is required"));
    }

    // -----------------------------------------------------------------------
    // Sign-up Tests
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("a sign-up creates a settings row with the defaults")
    void signUpCreatesDefaultSettings() throws Exception {
        mockMvc.perform(post("/backend/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "settings-user",
                                  "email": "settings-user@fitness.local",
                                  "password": "Password1"
                                }
                                """))
                .andExpect(status().isOk());

        flushAndClear();

        Map<String, Object> row = jdbcTemplate.queryForMap(
                "SELECT weight_unit, distance_unit, time_zone FROM user_settings WHERE user_id = ?",
                userId("settings-user"));
        assertThat(row)
                .containsEntry("weight_unit", "KG")
                .containsEntry("distance_unit", "KM")
                .containsEntry("time_zone", "Europe/Berlin");
    }
}
