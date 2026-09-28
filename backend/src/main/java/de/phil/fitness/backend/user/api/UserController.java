package de.phil.fitness.backend.user.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.phil.fitness.backend.auth.CurrentUser;
import de.phil.fitness.backend.common.ErrorResponse;
import de.phil.fitness.backend.user.dto.PasswordUpdateRequest;
import de.phil.fitness.backend.user.dto.UserDeleteRequest;
import de.phil.fitness.backend.user.dto.UserResponse;
import de.phil.fitness.backend.user.dto.UserUpdateRequest;
import de.phil.fitness.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Account of the authenticated user. There is no id in the path, the user is always the token subject.
 */
@RestController
@RequestMapping("/backend/me")
@Tag(name = "User", description = "Account of the authenticated user")
public class UserController {

    private final UserService userService;
    private final CurrentUser currentUser;

    public UserController(UserService userService, CurrentUser currentUser) {
        this.userService = userService;
        this.currentUser = currentUser;
    }

    @GetMapping
    @Operation(summary = "Get the own account")
    @ApiResponse(responseCode = "200", description = "The authenticated user")
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or the token's user no longer exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UserResponse> getUser() {
        return ResponseEntity.ok(userService.getUser(currentUser.currentUserId()));
    }

    @DeleteMapping
    @Operation(summary = "Delete the own account", description = "Deletes the user and everything the user owns. Irreversible, confirmed by the current password.")
    @ApiResponse(responseCode = "204", description = "Account deleted")
    @ApiResponse(responseCode = "400", description = "INVALID_JSON or VALIDATION_FAILED",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, "
            + "or the token's user no longer exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "INVALID_PASSWORD: the password does not match, the session stays valid",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> deleteUser(@Valid @RequestBody UserDeleteRequest req) {
        userService.deleteUser(currentUser.currentUserId(), req);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    @Operation(summary = "Update username and email", description = "Replaces both values, confirmed by the current "
            + "password. Resubmitting the own username or email is not a conflict. Existing tokens stay valid.")
    @ApiResponse(responseCode = "200", description = "The updated user")
    @ApiResponse(responseCode = "400", description = "INVALID_JSON or VALIDATION_FAILED",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or the token's user no longer exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "INVALID_PASSWORD: the password does not match, the session stays valid",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "USERNAME_ALREADY_TAKEN or EMAIL_ALREADY_EXISTS: another user has the value",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UserUpdateRequest req) {
        return ResponseEntity.ok(userService.updateUser(currentUser.currentUserId(), req));
    }

    @PutMapping("/password")
    @Operation(summary = "Change the password", description = "Confirmed by the current password. Tokens issued before the change stay valid until they expire.")
    @ApiResponse(responseCode = "204", description = "Password changed")
    @ApiResponse(responseCode = "400", description = "INVALID_JSON or VALIDATION_FAILED",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, "
            + "or the token's user no longer exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "INVALID_PASSWORD: the current password does not match, the session stays valid",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody PasswordUpdateRequest req) {
        userService.updatePassword(currentUser.currentUserId(), req);
        return ResponseEntity.noContent().build();
    }
}
