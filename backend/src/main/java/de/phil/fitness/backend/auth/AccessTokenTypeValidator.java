package de.phil.fitness.backend.auth;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Rejects every token that is not an access token.
 *
 * <p>Signature and expiry alone do not distinguish the two token kinds, so without this check the
 * long-lived refresh token would open every protected endpoint.
 */
public class AccessTokenTypeValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error WRONG_TYPE = new OAuth2Error(
            "invalid_token",
            "Only access tokens are accepted on protected endpoints",
            null);

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        String type = token.getClaimAsString(JwtService.TOKEN_TYPE_CLAIM);
        if (JwtService.TOKEN_TYPE_ACCESS.equals(type)) {
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(WRONG_TYPE);
    }
}
