package de.phil.fitness.backend.exercise.api;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import de.phil.fitness.backend.auth.CurrentUser;
import de.phil.fitness.backend.common.ErrorResponse;
import de.phil.fitness.backend.exercise.dto.ExerciseResponse;
import de.phil.fitness.backend.exercise.dto.ExerciseRequest;
import de.phil.fitness.backend.exercise.service.ExerciseService;
import de.phil.fitness.backend.exercise.dto.UpdateExerciseRequest;

/**
 * Controller showing the exercise catalog.
 */
@RestController
@RequestMapping("/backend/exercises")
@Tag(name = "Exercises", description = "Exercise catalog of the authenticated user")
public class ExerciseController {

    private final ExerciseService exerciseService;
    private final CurrentUser currentUser;

    public ExerciseController(ExerciseService exerciseService, CurrentUser currentUser) {
        this.exerciseService = exerciseService;
        this.currentUser = currentUser;
    }

    /**
     * Returns the exercises available to the authenticated user.
     * @return the global catalog merged with the caller's own definitions
     */
    @GetMapping
    @Operation(summary = "List available exercises", description = "Global catalog merged with the caller's own, "
            + "non-deleted definitions. Query parameters for filtering are not supported yet.")
    @ApiResponse(responseCode = "200", description = "Exercises available to the caller")
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<ExerciseResponse>> getExercises() {
        return ResponseEntity.ok(exerciseService.findAvailable(currentUser.currentUserId()));
    }

    /**
     * Returns exercise with certain id if available for the authenticated user.
     * @param id exercise id
     * @return the specific exercise
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a single exercise by id", description = "Returns the exercise with the given id, "
            + "provided it is part of the global catalog or owned by the requesting user.")
    @ApiResponse(responseCode = "200", description = "The requested exercise")
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "EXERCISE_NOT_FOUND: no exercise with this id is available to the caller",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ExerciseResponse> getExerciseById(@PathVariable Long id) {
        return ResponseEntity.ok(exerciseService.findExerciseById(currentUser.currentUserId(), id));
    }

    /**
     * Creates an exercise owned by the authenticated user.
     * @param req the exercise to create
     * @return the created exercise, with its location in the {@code Location} header
     */
    @PostMapping()
    @Operation(summary = "Create an own exercise", description = "Stores a new exercise visible only to the "
            + "requesting user. The global catalog cannot be extended through this endpoint.")
    @ApiResponse(responseCode = "201", description = "The created exercise")
    @ApiResponse(responseCode = "400", description = "VALIDATION_FAILED: a field is missing, too long or, for "
            + "exerciseType or trackingType, not one of their enum values",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ExerciseResponse> createExercise(@Valid @RequestBody ExerciseRequest req) {
        ExerciseResponse created = exerciseService.createNewExercise(req, currentUser.currentUserId());
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/backend/exercises/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Updates an exercise owned by the authenticated user.
     * @param id exercise id
     * @param req the complete new state of the exercise
     * @return the updated exercise
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an own exercise", description = "Replaces all fields of the exercise, provided it is "
            + "owned by the caller. An omitted description or instructions is cleared. The tracking type can only be "
            + "changed while no routine references the exercise.")
    @ApiResponse(responseCode = "200", description = "The updated exercise")
    @ApiResponse(responseCode = "400", description = "INVALID_JSON or VALIDATION_FAILED: a field is missing, too long or, for "
            + "exerciseType or trackingType, not one of their enum values",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "GLOBAL_EXERCISE_NOT_EDITABLE: the exercise is part of the global catalog",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "EXERCISE_NOT_FOUND: no exercise with this id is available to the caller",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "TRACKING_TYPE_LOCKED: the tracking type changes while the exercise "
            + "is referenced, or DATA_CONFLICT: the caller already has an exercise with this name",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ExerciseResponse> updateExercise(@PathVariable Long id, @Valid @RequestBody UpdateExerciseRequest req) {
        return ResponseEntity.ok(exerciseService.updateExercise(currentUser.currentUserId(), id, req));
    }

    /**
     * Deletes an exercise owned by the authenticated user.
     * @param id exercise id
     * @return no content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an own exercise", description = "Deletes the exercise, provided it is owned by the "
            + "caller. It is also removed from all of the caller's routines, the remaining exercises keep their order. "
            + "Workouts keep the exercise in their history. Its name can be used for a new exercise afterwards.")
    @ApiResponse(responseCode = "204", description = "Exercise deleted")
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "403", description = "GLOBAL_EXERCISE_NOT_EDITABLE: the exercise is part of the global catalog",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "EXERCISE_NOT_FOUND: no exercise with this id is available to the "
            + "caller, including one that is already deleted",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteUserExercise(currentUser.currentUserId(), id);
        return ResponseEntity.noContent().build();
    }
}
