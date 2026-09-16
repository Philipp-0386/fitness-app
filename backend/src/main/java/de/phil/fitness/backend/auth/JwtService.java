package de.phil.fitness.backend.auth;

import java.util.Date;
import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

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
}
