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

import de.phil.fitness.backend.common.ErrorResponse;
import de.phil.fitness.backend.exercise.dto.MuscleGroupResponse;
import de.phil.fitness.backend.exercise.service.MuscleGroupService;

/**
 * Controller serving the muscle group lookup.
 */
@RestController
@RequestMapping("/backend/muscle-groups")
@Tag(name = "Muscle groups", description = "Muscle groups and their areas, the same for every user")
public class MuscleGroupController {

    private final MuscleGroupService muscleGroupService;

    public MuscleGroupController(MuscleGroupService muscleGroupService) {
        this.muscleGroupService = muscleGroupService;
    }

    /**
     * Returns all muscle groups with their areas.
     * @return the top level groups, each with its areas
     */
    @GetMapping
    @Operation(summary = "List muscle groups", description = "Top level groups with their body region and areas, in a "
            + "fixed order. The ids match groupId and id in the muscles of an exercise.")
    @ApiResponse(responseCode = "200", description = "All muscle groups")
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<MuscleGroupResponse>> getMuscleGroups() {
        return ResponseEntity.ok(muscleGroupService.findAll());
    }
}
