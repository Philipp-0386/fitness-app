package de.phil.fitness.backend.logout.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.phil.fitness.backend.common.ErrorResponse;
import de.phil.fitness.backend.logout.dto.LogoutRequest;
import de.phil.fitness.backend.logout.service.LogoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller responsible for ending a session.
 */
@RestController
@RequestMapping("/backend/auth")
@Tag(name = "Auth", description = "Public endpoints for account creation and login")
@SecurityRequirements
public class LogoutController {
    private final LogoutService logoutService;

    public LogoutController(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    @PostMapping("/logout")
    @Operation(summary = "Log out", description = "Revokes the session of the refresh token, every refresh token of it "
            + "is rejected afterwards. Access tokens already issued stay valid until they expire. An unusable refresh "
            + "token is accepted as well, there is nothing left to revoke.")
    @ApiResponse(responseCode = "204", description = "Session ended")
    @ApiResponse(responseCode = "400", description = "INVALID_JSON or VALIDATION_FAILED",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        logoutService.logout(request);
        return ResponseEntity.noContent().build();
    }
}
