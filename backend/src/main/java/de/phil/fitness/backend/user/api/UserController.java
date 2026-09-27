package de.phil.fitness.backend.user.api;

import de.phil.fitness.backend.user.dto.PasswordUpdateRequest;
import de.phil.fitness.backend.user.dto.UserUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.phil.fitness.backend.auth.CurrentUser;
import de.phil.fitness.backend.user.dto.UserDeleteRequest;
import de.phil.fitness.backend.user.dto.UserResponse;
import de.phil.fitness.backend.user.service.UserService;
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
    public ResponseEntity<UserResponse> getUser() {
        return ResponseEntity.ok(userService.getUser(currentUser.currentUserId()));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@Valid @RequestBody UserDeleteRequest req) {
        userService.deleteUser(currentUser.currentUserId(), req);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UserUpdateRequest req) {
        return ResponseEntity.ok(userService.updateUser(currentUser.currentUserId(), req));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@Valid @RequestBody PasswordUpdateRequest req) {
        userService.updatePassword(currentUser.currentUserId(), req);
        return ResponseEntity.noContent().build();
    }
}
