package de.phil.fitness.backend.tokenRefresh.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.phil.fitness.backend.common.ErrorResponse;
import de.phil.fitness.backend.tokenRefresh.dto.AccessRefreshRequest;
import de.phil.fitness.backend.tokenRefresh.dto.AccessRefreshResponse;
import de.phil.fitness.backend.tokenRefresh.service.TokenRefreshService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller responsible for exchanging a refresh token for a new access token.
 */
@RestController
@RequestMapping("/backend/auth")
@Tag(name = "Auth", description = "Public endpoints for account creation and login")
@SecurityRequirements
public class TokenRefreshController {
    private final TokenRefreshService tokenRefreshService;

    public TokenRefreshController(TokenRefreshService tokenRefreshService) {
        this.tokenRefreshService = tokenRefreshService;
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh the access token", description = "Exchanges a valid refresh token for a new 2h "
            + "access token. The refresh token itself is not renewed.")
    @ApiResponse(responseCode = "200", description = "Refresh token valid")
    @ApiResponse(responseCode = "400", description = "INVALID_JSON or VALIDATION_FAILED",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: invalid or expired refresh token, an access "
            + "token, or the token's user no longer exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public AccessRefreshResponse refresh(@Valid @RequestBody AccessRefreshRequest request) {
        return tokenRefreshService.refresh(request);
    }
}
