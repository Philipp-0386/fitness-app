package de.phil.fitness.backend.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import de.phil.fitness.backend.auth.exception.InvalidRefreshTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    /** Claim that distinguishes an access token from a refresh token. */
    public static final String TOKEN_TYPE_CLAIM = "type";
    public static final String TOKEN_TYPE_ACCESS = "access";
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    /** Claim of a refresh token naming its session, the unit a logout revokes. */
    public static final String SESSION_ID_CLAIM = "sid";

    private final JwtProperties jwtProperties;
    private final SecretKey key;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes());
    }

    public String generateAccessToken(String userId) {
        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofMinutes(jwtProperties.accessTokenExpirationMinutes()));

        return Jwts.builder()
                .subject(userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_ACCESS)
                .signWith(key)
                .compact();
    }

    /**
     * @param userId the user the token is issued to
     * @param sessionId the session of the token, new at login and carried over on every refresh
     */
    public String generateRefreshToken(String userId, UUID sessionId) {
        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofDays(jwtProperties.refreshTokenExpirationDays()));

        return Jwts.builder()
                .subject(userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_REFRESH)
                .claim(SESSION_ID_CLAIM, sessionId.toString())
                .signWith(key)
                .compact();
    }

    /**
     * Verifies a refresh token and returns its claims. Revocation is not checked here.
     * @param token the given refresh token from the client
     * @return user id, session id and issue time of the token
     * @throws InvalidRefreshTokenException thrown if the token is invalid (malformed, expired, wrongly signed, not a
     *         refresh token or missing a claim)
     */
    public RefreshTokenClaims verifyRefreshToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidRefreshTokenException("Refresh token could not be verified: " + e.getMessage());
        }

        if (!TOKEN_TYPE_REFRESH.equals(claims.get(TOKEN_TYPE_CLAIM, String.class))) {
            throw new InvalidRefreshTokenException("Token is not a refresh token");
        }

        String sessionId = claims.get(SESSION_ID_CLAIM, String.class);
        if (sessionId == null || claims.getIssuedAt() == null) {
            throw new InvalidRefreshTokenException("Refresh token has no session id or issue time");
        }

        try {
            return new RefreshTokenClaims(
                    Long.valueOf(claims.getSubject()),
                    UUID.fromString(sessionId),
                    claims.getIssuedAt().toInstant());
        } catch (IllegalArgumentException e) {
            throw new InvalidRefreshTokenException("Refresh token subject is not a user id or session id is not a UUID");
        }
    }
}
