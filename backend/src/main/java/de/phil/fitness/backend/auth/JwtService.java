package de.phil.fitness.backend.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

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

    public String generateRefreshToken(String userId) {
        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofDays(jwtProperties.refreshTokenExpirationDays()));

        return Jwts.builder()
                .subject(userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claim(TOKEN_TYPE_CLAIM, TOKEN_TYPE_REFRESH)
                .signWith(key)
                .compact();
    }

    /**
     * Verifies a refresh token and returns the id of the user it was issued to.
     * @param token the given refresh token from the client
     * @return user id taken from the token's subject claim
     * @throws InvalidRefreshTokenException thrown if the token is invalid (malformed, expired, wrongly signed or not a refresh token)
     */
    public Long verifyRefreshToken(String token) {
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

        try {
            return Long.valueOf(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new InvalidRefreshTokenException("Refresh token subject is not a user id");
        }
    }
}
