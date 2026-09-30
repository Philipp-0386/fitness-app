package de.phil.fitness.backend.tokenRefresh.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.phil.fitness.backend.tokenRefresh.dto.AccessRefreshRequest;
import de.phil.fitness.backend.tokenRefresh.dto.AccessRefreshResponse;
import de.phil.fitness.backend.tokenRefresh.service.TokenRefreshService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;

/**
 * Controller responsible for exchanging a refresh token for a new access token.
 */
@RestController
@RequestMapping("/backend/auth")
@SecurityRequirements
public class TokenRefreshController {
    private final TokenRefreshService tokenRefreshService;

    public TokenRefreshController(TokenRefreshService tokenRefreshService) {
        this.tokenRefreshService = tokenRefreshService;
    }

    @PostMapping("/refresh")
    public AccessRefreshResponse refresh(@Valid @RequestBody AccessRefreshRequest request) {
        return tokenRefreshService.refresh(request);
    }
}
