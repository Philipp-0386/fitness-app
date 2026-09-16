package de.phil.fitness.backend.auth;

import de.phil.fitness.backend.auth.exception.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Provides access to the authenticated user of the current request.
 *
 * <p>Reads the principal that the resource server placed into the security context after it
 * validated the bearer token. It does not decode or verify tokens itself.
 */
@Component
public class CurrentUser {

    /**
     * Returns the id of the user the current request is authenticated as.
     * @return the user id taken from the token's subject claim.
     * @throws AccessDeniedException if the request is not authenticated with a JWT, which the
     *         controller advice reports as 403. With anyRequest().authenticated() in place the
     *         filter chain rejects such requests before they reach a controller.
     */
    public Long currentUserId() {
        return Long.valueOf(currentToken().getSubject());
    }

    private Jwt currentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
            return jwtAuthentication.getToken();
        }
        throw new AccessDeniedException("No authenticated JWT in the security context.");
    }
}
