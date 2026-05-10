package de.phil.fitness.backend.config;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Service responsible for generating and validating JWT tokens. It uses a secret key to sign the tokens and includes user details such as username and roles in the token claims. The service also provides methods to extract information from the token and validate its authenticity and expiration.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:900000}")
    private long expiration;

    /**
     * Generates a secret key for signing JWT tokens based on the configured secret string.
     * @return a SecretKey object derived from the base64-encoded secret string, used for signing JWT tokens
     */
    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    /**
     * Generates a JWT token for the given user details.
     * @param user the user details for which to generate the token
     * @return a JWT token containing the username and roles of the user, signed with the secret key
     */
    public String generate(UserDetails user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key())
                .compact();
    }

    /**
     * Extracts the username from the JWT token.
     * @param token the JWT token from which to extract the username
     * @return the username contained in the token, or null if the token is invalid
     */
    public String extractUsername(String token) {
        return claims(token).getSubject();
    }

    /**
     * 
     * @param token
     * @param user
     * @return
     */
    public boolean isValid(String token, UserDetails user) {
        try {
            return extractUsername(token).equals(user.getUsername()) && !claims(token).getExpiration().before(new Date());
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Extracts the claims from the JWT token, which contain the payload data such as the username, roles, and expiration time.
     * @param token the JWT token from which to extract the claims
     * @return the Claims object containing the data from the token, or throws a JwtException if the token is invalid or cannot be parsed
     */
    private Claims claims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
