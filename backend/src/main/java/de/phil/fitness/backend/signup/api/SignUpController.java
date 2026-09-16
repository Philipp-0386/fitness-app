package de.phil.fitness.backend.signup.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.phil.fitness.backend.common.ErrorResponse;
import de.phil.fitness.backend.signup.dto.SignUpRequest;
import de.phil.fitness.backend.signup.dto.SignUpResponse;
import de.phil.fitness.backend.signup.service.SignUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST endpoint responsible for handling user creation process.
 */
@RestController
@RequestMapping("/backend/auth")
@Tag(name = "Auth", description = "Public endpoints for account creation and login")
@SecurityRequirements
public class SignUpController {
    private final SignUpService signUpService;

    public SignUpController(SignUpService sus) {
        this.signUpService = sus;
    }

    /**
     * Default endpoint for user creation.
     * @param request Contains user information after automatically mapped by Jackson
     * @return Returns {@link SignUpResponse} object after successful creation process
     */
    @PostMapping("/signup")
    @Operation(summary = "Create an account", description = "dateOfBirth is expected as yyyy-MM-dd. Does not log the user in.")
    @ApiResponse(responseCode = "200", description = "Account created")
    @ApiResponse(responseCode = "400", description = "INVALID_JSON or VALIDATION_FAILED",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "EMAIL_ALREADY_EXISTS or USERNAME_ALREADY_TAKEN",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "DEFAULT_ROLE_NOT_FOUND (missing seed data)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    //Frontend hinweis: yyyy-MM-dd für dateOfBirth angeben (input date type)
    public SignUpResponse root(@Valid @RequestBody SignUpRequest request) {
        return signUpService.createUser(request);
    }
}
