package de.phil.fitness.backend.routine.api;

import de.phil.fitness.backend.auth.CurrentUser;
import de.phil.fitness.backend.common.ErrorResponse;
import de.phil.fitness.backend.routine.dto.RoutineDetailResponse;
import de.phil.fitness.backend.routine.dto.CreateRoutineRequest;
import de.phil.fitness.backend.routine.dto.RoutineResponse;
import de.phil.fitness.backend.routine.service.RoutineService;
import de.phil.fitness.backend.routine.dto.UpdateRoutineRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controller for the routines of the authenticated user and the preset routines available to everyone.
 */
@RestController
@RequestMapping("/backend/routines")
@Tag(name = "Routines", description = "Own routines of the authenticated user and system preset routines")
public class RoutineController {
    private final RoutineService routineService;
    private final CurrentUser currentUser;

    public RoutineController(RoutineService routineService,  CurrentUser currentUser) {
        this.routineService = routineService;
        this.currentUser = currentUser;
    }

    @GetMapping
    @Operation(summary = "List own routines", description = "Routines owned by the caller, without their exercises.")
    @ApiResponse(responseCode = "200", description = "Routines owned by the caller")
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public List<RoutineResponse> getUserRoutines() {
        return routineService.findUserRoutines(currentUser.currentUserId());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an own routine by id", description = "Returns the routine including its exercises, "
            + "provided it is owned by the caller.")
    @ApiResponse(responseCode = "200", description = "The requested routine with its exercises")
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "USER_ROUTINE_NOT_FOUND: no routine with this id is owned by the caller",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public RoutineDetailResponse getUserRoutine(@PathVariable Long id) {
        return routineService.findDetailedUserRoutine(currentUser.currentUserId(), id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an own routine", description = "Deletes the routine including its exercises, provided "
            + "it is owned by the caller. Workouts and program days referencing it keep existing without the reference.")
    @ApiResponse(responseCode = "204", description = "Routine deleted")
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "USER_ROUTINE_NOT_FOUND: no routine with this id is owned by the caller",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> deleteUserRoutine(@PathVariable Long id) {
        routineService.deleteUserRoutine(currentUser.currentUserId(), id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an own routine", description = "Replaces name, description and exercises of the routine, "
            + "provided it is owned by the caller. The body describes the complete new state: an omitted description is "
            + "cleared and omitted exercises are removed. The order of the exercises is taken from the list.")
    @ApiResponse(responseCode = "200", description = "The updated routine with its exercises")
    @ApiResponse(responseCode = "400", description = "INVALID_JSON or VALIDATION_FAILED: name missing or too long, "
            + "description too long, more than 50 exercises, an exerciseId missing, a non-positive target value "
            + "targetRepsMin above targetRepsMax, or a target the tracking type of its exercise does not use",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "USER_ROUTINE_NOT_FOUND: no routine with this id is owned by the caller, "
            + "or EXERCISE_NOT_FOUND: a referenced exercise is not available to the caller",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public RoutineDetailResponse updateUserRoutine(@PathVariable Long id, @Valid @RequestBody UpdateRoutineRequest request) {
        return routineService.updateUserRoutine(currentUser.currentUserId(), id, request);
    }

    @GetMapping("/presets")
    @Operation(summary = "List preset routines", description = "Routines defined by the system for all users, "
            + "without their exercises.")
    @ApiResponse(responseCode = "200", description = "All preset routines")
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public List<RoutineResponse> getPresetRoutines() {
        return routineService.findPresetRoutines();
    }

    @GetMapping("/presets/{id}")
    @Operation(summary = "Get a preset routine by id", description = "Returns the preset routine including its exercises.")
    @ApiResponse(responseCode = "200", description = "The requested preset routine with its exercises")
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "PRESET_ROUTINE_NOT_FOUND: no preset routine with this id exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public RoutineDetailResponse getPresetRoutine(@PathVariable Long id) {
        return routineService.findDetailedPresetRoutine(id);
    }

    @PostMapping
    @Operation(summary = "Create an own routine", description = "Stores a new routine owned by the caller, including "
            + "its exercises if any are given. The order of the exercises is taken from the list.")
    @ApiResponse(responseCode = "201", description = "The created routine with its exercises")
    @ApiResponse(responseCode = "400", description = "INVALID_JSON or VALIDATION_FAILED: name missing or too long, "
            + "description too long, more than 50 exercises, an exerciseId missing, a non-positive target value "
            + "targetRepsMin above targetRepsMax, or a target the tracking type of its exercise does not use",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "EXERCISE_NOT_FOUND: a referenced exercise is not available to the caller",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<RoutineDetailResponse> createUserRoutine(@Valid @RequestBody CreateRoutineRequest routineRequest) {
        RoutineDetailResponse created = routineService.createNewUserRoutine(routineRequest, currentUser.currentUserId());
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/backend/routines/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
}
