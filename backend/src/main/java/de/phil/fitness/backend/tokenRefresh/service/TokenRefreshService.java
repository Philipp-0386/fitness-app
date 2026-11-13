package de.phil.fitness.backend.tokenRefresh.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import de.phil.fitness.backend.auth.JwtService;
import de.phil.fitness.backend.auth.RefreshTokenClaims;
import de.phil.fitness.backend.auth.exception.InvalidRefreshTokenException;
import de.phil.fitness.backend.auth.repository.RevokedSessionRepository;
import de.phil.fitness.backend.tokenRefresh.dto.AccessRefreshRequest;
import de.phil.fitness.backend.tokenRefresh.dto.AccessRefreshResponse;
import de.phil.fitness.backend.user.model.User;
import de.phil.fitness.backend.user.service.UserService;

@Service
public class TokenRefreshService {

    private final JwtService jwtService;
    private final UserService userService;
    private final RevokedSessionRepository revokedSessionRepository;

    public TokenRefreshService(JwtService jwtService, UserService userService,
                               RevokedSessionRepository revokedSessionRepository) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.revokedSessionRepository = revokedSessionRepository;
    }

    /**
     * Issues a new access token and a new refresh token of the same session, so the session lasts as long as it is used.
     * The old refresh token is not revoked, it stays valid until it expires.
     * @throws InvalidRefreshTokenException if the token is invalid, its user is gone, its session was logged out or it
     *         was issued before the user's tokens_valid_after
     */
    public AccessRefreshResponse refresh(AccessRefreshRequest request) {
        RefreshTokenClaims claims = jwtService.verifyRefreshToken(request.refreshToken());

        User user = userService.findById(claims.userId())
                .orElseThrow(() -> new InvalidRefreshTokenException(
                        "Refresh token subject has no user. userId=" + claims.userId()));

        if (revokedSessionRepository.existsById(claims.sessionId())) {
            throw new InvalidRefreshTokenException("Refresh token belongs to a logged out session. userId=" + user.getId());
        }

        Instant validAfter = user.getTokensValidAfter();
        if (validAfter != null && claims.issuedAt().isBefore(validAfter)) {
            throw new InvalidRefreshTokenException("Refresh token issued before tokens_valid_after. userId=" + user.getId());
        }

        String userId = user.getId().toString();
        return new AccessRefreshResponse(
                jwtService.generateAccessToken(userId),
                jwtService.generateRefreshToken(userId, claims.sessionId()));
    }
}
