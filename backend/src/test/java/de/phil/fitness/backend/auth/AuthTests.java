package de.phil.fitness.backend.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

import de.phil.fitness.backend.TestDatabase;
import de.phil.fitness.backend.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Covers the front door: who gets a token, and which tokens the resource server accepts.
 *
 * <p>Tokens are real ones produced by {@link JwtService} or signed by hand with the configured
 * secret. The {@code jwt()} post-processor from spring-security-test is deliberately not used:
 * it injects a ready-made Authentication and bypasses the decoder, so neither the signature
 * check nor {@link AccessTokenTypeValidator} would run.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"dev", "test"})
@Import(TestDatabase.class)
class AuthTests {

    private static final String PROTECTED_PATH = "/backend/exercises";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private UserRepository userRepository;

    private String accessTokenFor(String username) {
        Long id = userRepository.findByUsername(username).orElseThrow().getId();
        return jwtService.generateAccessToken(id.toString());
    }

    // -----------------------------------------------------------------------
    // Login Tests
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("valid credentials yield an access and a refresh token")
    void loginWithValidCredentialsReturnsTokenPair() throws Exception {
        mockMvc.perform(post("/backend/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "max", "password": "password"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    @DisplayName("a wrong password and an unknown user are indistinguishable")
    void failedLoginsDoNotRevealWhetherTheUserExists() throws Exception {
        String wrongPassword = mockMvc.perform(post("/backend/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "max", "password": "not-the-password"}
                                """))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        String unknownUser = mockMvc.perform(post("/backend/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "does-not-exist", "password": "password"}
                                """))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        // Only the timestamp may differ; code and message must not hint at which half failed,
        // otherwise the endpoint becomes a user enumeration oracle.
        assertThat(codeAndMessageOf(wrongPassword)).isEqualTo(codeAndMessageOf(unknownUser));
    }

    // -----------------------------------------------------------------------
    // Token Acceptance Tests
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("a valid access token reaches the protected endpoint")
    void validAccessTokenIsAccepted() throws Exception {
        mockMvc.perform(get(PROTECTED_PATH).header("Authorization", "Bearer " + accessTokenFor("max")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("no Authorization header is rejected")
    void missingTokenIsRejected() throws Exception {
        mockMvc.perform(get(PROTECTED_PATH))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
    }

    @Test
    @DisplayName("a token that is not a JWT is rejected")
    void malformedTokenIsRejected() throws Exception {
        mockMvc.perform(get(PROTECTED_PATH).header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
    }

    @Test
    @DisplayName("a valid structured token signed with a foreign key is rejected")
    void foreignSignatureIsRejected() throws Exception {
        SecretKey foreignKey = Keys.hmacShaKeyFor(
                "a-different-secret-that-is-long-enough-for-hs512-signing-abcdefgh".getBytes());

        String forged = Jwts.builder()
                .subject("2")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(Duration.ofMinutes(15))))
                .claim(JwtService.TOKEN_TYPE_CLAIM, JwtService.TOKEN_TYPE_ACCESS)
                .signWith(foreignKey)
                .compact();

        mockMvc.perform(get(PROTECTED_PATH).header("Authorization", "Bearer " + forged))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
    }

    @Test
    @DisplayName("an expired access token is rejected")
    void expiredTokenIsRejected() throws Exception {
        Instant issued = Instant.now().minus(Duration.ofHours(2));

        String expired = Jwts.builder()
                .subject("2")
                .issuedAt(Date.from(issued))
                .expiration(Date.from(issued.plus(Duration.ofMinutes(15))))
                .claim(JwtService.TOKEN_TYPE_CLAIM, JwtService.TOKEN_TYPE_ACCESS)
                .signWith(Keys.hmacShaKeyFor(jwtProperties.secret().getBytes()))
                .compact();

        mockMvc.perform(get(PROTECTED_PATH).header("Authorization", "Bearer " + expired))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
    }

    @Test
    @DisplayName("a refresh token is not accepted as a bearer token")
    void refreshTokenIsNotAnAccessToken() throws Exception {
        Long maxId = userRepository.findByUsername("max").orElseThrow().getId();
        String refreshToken = jwtService.generateRefreshToken(maxId.toString());

        mockMvc.perform(get(PROTECTED_PATH).header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
    }

    // -----------------------------------------------------------------------
    // Sign-up Tests 
    // -----------------------------------------------------------------------

    @Test
    @Transactional
    @DisplayName("a taken username is refused")
    void signUpWithTakenUsernameIsRefused() throws Exception {
        mockMvc.perform(post("/backend/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "max",
                                  "email": "brand-new-address@fitness.local",
                                  "password": "Password1"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("USERNAME_ALREADY_TAKEN"))
                .andExpect(jsonPath("$.message").value("Username already taken!"));
    }

    @Test
    @Transactional
    @DisplayName("a registered email is refused")
    void signUpWithRegisteredEmailIsRefused() throws Exception {
        mockMvc.perform(post("/backend/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "brand-new-name",
                                  "email": "max@fitness.local",
                                  "password": "Password1"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    @Transactional
    @DisplayName("an invalid body is reported per field")
    void signUpWithInvalidBodyReportsFieldErrors() throws Exception {
        mockMvc.perform(post("/backend/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "ab",
                                  "email": "not-an-email",
                                  "password": "short"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.username").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    @Transactional
    @DisplayName("sign-up succeeds with username, email and password only")
    void signUpWithRequiredFieldsSucceeds() throws Exception {
        mockMvc.perform(post("/backend/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "new-user",
                                  "email": "new-user@fitness.local",
                                  "password": "Password1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("new-user"));
    }

    private String codeAndMessageOf(String body) {
        return JsonPath.read(body, "$.code") + "|" + JsonPath.read(body, "$.message");
    }
}
