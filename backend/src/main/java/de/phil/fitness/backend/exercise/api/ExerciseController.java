package de.phil.fitness.backend.exercise.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import de.phil.fitness.backend.auth.CurrentUser;
import de.phil.fitness.backend.common.ErrorResponse;
import de.phil.fitness.backend.exercise.dto.ExerciseResponse;
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
}
