package de.phil.fitness.backend.login.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.phil.fitness.backend.common.ErrorResponse;
import de.phil.fitness.backend.login.dto.LoginRequest;
import de.phil.fitness.backend.login.dto.LoginResponse;
import de.phil.fitness.backend.login.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller responsible for handling login requests.
 */
@RestController
@RequestMapping("/backend/auth")
@Tag(name = "Auth", description = "Public endpoints for account creation and login")
@SecurityRequirements
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    @Operation(summary = "Log in", description = "Returns a 15 min access token and a 7 day refresh token. "
            + "Unknown user and wrong password produce the identical response.")
    @ApiResponse(responseCode = "200", description = "Credentials valid")
    @ApiResponse(responseCode = "400", description = "INVALID_JSON or VALIDATION_FAILED",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "INVALID_CREDENTIALS",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<LoginResponse> handleLoginRequest(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = loginService.login(request);
        return ResponseEntity.ok(response);
    }
}
