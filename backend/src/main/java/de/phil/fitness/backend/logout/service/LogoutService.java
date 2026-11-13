package de.phil.fitness.backend.logout.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.phil.fitness.backend.auth.JwtProperties;
import de.phil.fitness.backend.auth.JwtService;
import de.phil.fitness.backend.auth.RefreshTokenClaims;
import de.phil.fitness.backend.auth.exception.InvalidRefreshTokenException;
import de.phil.fitness.backend.auth.model.RevokedSession;
import de.phil.fitness.backend.auth.repository.RevokedSessionRepository;
import de.phil.fitness.backend.logout.dto.LogoutRequest;
import de.phil.fitness.backend.user.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LogoutService {

    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final UserService userService;
    private final RevokedSessionRepository revokedSessionRepository;

    public LogoutService(JwtService jwtService, JwtProperties jwtProperties, UserService userService,
                         RevokedSessionRepository revokedSessionRepository) {
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.userService = userService;
        this.revokedSessionRepository = revokedSessionRepository;
    }

    /**
     * Revokes the session of the refresh token, so no refresh token of it is accepted anymore. A token that is
     * already unusable (invalid, expired, user gone) needs no revocation and is ignored.
     *
     * <p>The row is kept for one refresh token lifetime from now, the latest moment a token of the session can expire.
     * Rows past that are deleted on the way.
     */
    @Transactional
    public void logout(LogoutRequest request) {
        RefreshTokenClaims claims;
        try {
            claims = jwtService.verifyRefreshToken(request.refreshToken());
        } catch (InvalidRefreshTokenException e) {
            log.info("Logout with an unusable refresh token, nothing to revoke");
            return;
        }
        if (!userService.existsById(claims.userId())) {
            return;
        }

        Instant now = Instant.now();
        revokedSessionRepository.deleteByExpiresAtBefore(now);
        revokedSessionRepository.save(new RevokedSession(
                claims.sessionId(),
                claims.userId(),
                now.plus(Duration.ofDays(jwtProperties.refreshTokenExpirationDays()))));
        log.info("Session revoked. userId={}", claims.userId());
    }
}
