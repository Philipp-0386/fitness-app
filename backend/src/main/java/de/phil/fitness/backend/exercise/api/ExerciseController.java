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
    @PostMapping("/new")
    @Operation(summary = "Create an own exercise", description = "Stores a new exercise visible only to the "
            + "requesting user. The global catalog cannot be extended through this endpoint.")
    @ApiResponse(responseCode = "201", description = "The created exercise")
    @ApiResponse(responseCode = "400", description = "VALIDATION_FAILED: a field is missing, too long or, for "
            + "exerciseType, not one of STRENGTH, CARDIO, MOBILITY",
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
}
