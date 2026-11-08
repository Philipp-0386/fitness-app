package de.phil.fitness.backend.workout.api;

import de.phil.fitness.backend.auth.CurrentUser;
import de.phil.fitness.backend.common.ErrorResponse;
import de.phil.fitness.backend.workout.dto.StartWorkoutRequest;
import de.phil.fitness.backend.workout.dto.WorkoutExerciseOrderRequest;
import de.phil.fitness.backend.workout.dto.WorkoutExerciseRequest;
import de.phil.fitness.backend.workout.dto.WorkoutExerciseResponse;
import de.phil.fitness.backend.workout.dto.WorkoutResponse;
import de.phil.fitness.backend.workout.dto.WorkoutSetRequest;
import de.phil.fitness.backend.workout.dto.WorkoutSetResponse;
import de.phil.fitness.backend.workout.service.WorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for the workouts of the authenticated user: starting, the running workout, finishing and discarding.
 */
@RestController
@RequestMapping("/backend/workouts")
@Tag(name = "Workouts", description = "Workouts of the authenticated user")
public class WorkoutController {
    private final CurrentUser currentUser;
    private final WorkoutService workoutService;

    public WorkoutController(CurrentUser currentUser, WorkoutService workoutService) {
        this.currentUser = currentUser;
        this.workoutService = workoutService;
    }

    @PostMapping
    @Operation(summary = "Start a workout", description = "Starts a workout from an own routine, given directly or "
            + "through a day of an own program. The routine's exercises become the workout's exercises in the same "
            + "order, their targets are copied as snapshots, soft deleted exercises are skipped. A running workout "
            + "without activity for four hours is closed first and does not block the start.")
    @ApiResponse(responseCode = "201", description = "The started workout with its exercises, without sets")
    @ApiResponse(responseCode = "400", description = "INVALID_JSON or VALIDATION_FAILED: not exactly one of routineId "
            + "and programDayId given, or REST_DAY_NOT_STARTABLE: the program day is a rest day",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "USER_ROUTINE_NOT_FOUND: no routine with this id is owned by the "
            + "caller, or PROGRAM_DAY_NOT_FOUND: no program day with this id is owned by the caller",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "WORKOUT_ALREADY_ACTIVE: the caller has another workout in progress, "
            + "or DATA_CONFLICT: a concurrent start won the race",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<WorkoutResponse> startWorkout(@Valid @RequestBody StartWorkoutRequest request) {
        WorkoutResponse started = workoutService.startWorkout(currentUser.currentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(started);
    }

    @GetMapping("/active")
    @Operation(summary = "Get the running workout", description = "Returns the workout the caller has in progress, "
            + "including its exercises and logged sets. A workout without activity for four hours is closed on the way: "
            + "completed at the time of its newest set, or deleted if it has no sets.")
    @ApiResponse(responseCode = "200", description = "The running workout")
    @ApiResponse(responseCode = "204", description = "The caller has no workout in progress", content = @Content)
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<WorkoutResponse> getActiveWorkout() {
        return workoutService.findActiveWorkout(currentUser.currentUserId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/{id}/finish")
    @Operation(summary = "Finish a workout", description = "Completes a running workout owned by the caller, its end "
            + "is the current server time.")
    @ApiResponse(responseCode = "204", description = "Workout finished")
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "WORKOUT_NOT_FOUND: no workout with this id is owned by the caller",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "WORKOUT_NOT_IN_PROGRESS: the workout is already finished, or "
            + "WORKOUT_HAS_NO_SETS: the workout has no logged set and should be discarded instead",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> finishWorkout(@PathVariable Long id) {
        workoutService.finishWorkout(currentUser.currentUserId(), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a workout", description = "Deletes a workout owned by the caller including its "
            + "exercises and sets, whether it is running or finished.")
    @ApiResponse(responseCode = "204", description = "Workout deleted")
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "WORKOUT_NOT_FOUND: no workout with this id is owned by the caller",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long id) {
        workoutService.deleteWorkout(currentUser.currentUserId(), id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/sets/{clientId}")
    @Operation(summary = "Log or replace a set", description = "Stores the set under the id the client generated for "
            + "it: the first request creates it, every further one replaces its values, so a retry never creates a "
            + "second set. Which values are required depends on the tracking type of the entry's exercise. Works on "
            + "running and finished workouts.")
    @ApiResponse(responseCode = "200", description = "The saved set")
    @ApiResponse(responseCode = "400", description = "INVALID_JSON, INVALID_PARAMETER: the client id is not a UUID, or "
            + "VALIDATION_FAILED: a value is missing, negative or too long, does not match the tracking type, or the "
            + "set would move to another exercise entry",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "WORKOUT_NOT_FOUND: no workout with this id is owned by the caller, "
            + "or the client id belongs to a set of another workout, or WORKOUT_EXERCISE_NOT_FOUND: the exercise "
            + "entry is not part of the workout",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "SET_NUMBER_TAKEN: another set of the entry already has this number, "
            + "or DATA_CONFLICT: a concurrent request won the race",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<WorkoutSetResponse> saveSet(@PathVariable Long id, @PathVariable UUID clientId,
                                                      @Valid @RequestBody WorkoutSetRequest request) {
        return ResponseEntity.ok(workoutService.saveSet(currentUser.currentUserId(), id, clientId, request));
    }

    @DeleteMapping("/{id}/sets/{clientId}")
    @Operation(summary = "Delete a set", description = "Deletes the set with this client id. A set that does not exist "
            + "or was already deleted is no error, so a retry succeeds.")
    @ApiResponse(responseCode = "204", description = "Set deleted or not present")
    @ApiResponse(responseCode = "400", description = "INVALID_PARAMETER: the client id is not a UUID",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "WORKOUT_NOT_FOUND: no workout with this id is owned by the caller",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> deleteSet(@PathVariable Long id, @PathVariable UUID clientId) {
        workoutService.deleteSet(currentUser.currentUserId(), id, clientId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/exercises")
    @Operation(summary = "Add an exercise", description = "Appends an exercise after the last entry of a workout owned "
            + "by the caller, without targets. Works on running and finished workouts.")
    @ApiResponse(responseCode = "201", description = "The new entry with the exercise's last time")
    @ApiResponse(responseCode = "400", description = "INVALID_JSON or VALIDATION_FAILED: exerciseId is missing",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "WORKOUT_NOT_FOUND: no workout with this id is owned by the caller, "
            + "or EXERCISE_NOT_FOUND: the exercise is not available to the caller or deleted",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "EXERCISE_ALREADY_IN_WORKOUT: another entry already has this exercise",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<WorkoutExerciseResponse> addExercise(@PathVariable Long id,
                                                               @Valid @RequestBody WorkoutExerciseRequest request) {
        WorkoutExerciseResponse added = workoutService.addExercise(currentUser.currentUserId(), id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(added);
    }

    @PutMapping("/{id}/exercises/order")
    @Operation(summary = "Reorder the exercises", description = "Sets the complete new order of a workout's entries. "
            + "The list must contain every entry exactly once; entries with sets move too.")
    @ApiResponse(responseCode = "204", description = "Order saved")
    @ApiResponse(responseCode = "400", description = "INVALID_JSON or VALIDATION_FAILED: entryIds is missing, misses an "
            + "entry, holds one twice or holds one of another workout",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "WORKOUT_NOT_FOUND: no workout with this id is owned by the caller",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> reorderExercises(@PathVariable Long id,
                                                 @Valid @RequestBody WorkoutExerciseOrderRequest request) {
        workoutService.reorderExercises(currentUser.currentUserId(), id, request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/exercises/{entryId}")
    @Operation(summary = "Swap an exercise", description = "Replaces the exercise of an entry that has no logged sets. "
            + "The position stays; the targets are kept as far as the new exercise's tracking type uses them.")
    @ApiResponse(responseCode = "200", description = "The changed entry with the new exercise's last time")
    @ApiResponse(responseCode = "400", description = "INVALID_JSON or VALIDATION_FAILED: exerciseId is missing",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "WORKOUT_NOT_FOUND: no workout with this id is owned by the caller, "
            + "WORKOUT_EXERCISE_NOT_FOUND: the entry is not part of the workout, or EXERCISE_NOT_FOUND: the exercise is "
            + "not available to the caller or deleted",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "WORKOUT_EXERCISE_HAS_SETS: the entry has logged sets, or "
            + "EXERCISE_ALREADY_IN_WORKOUT: another entry already has this exercise",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<WorkoutExerciseResponse> swapExercise(@PathVariable Long id, @PathVariable Long entryId,
                                                                @Valid @RequestBody WorkoutExerciseRequest request) {
        return ResponseEntity.ok(workoutService.swapExercise(currentUser.currentUserId(), id, entryId, request));
    }

    @DeleteMapping("/{id}/exercises/{entryId}")
    @Operation(summary = "Remove an exercise", description = "Removes an entry including its sets. The other entries "
            + "keep their order.")
    @ApiResponse(responseCode = "204", description = "Exercise removed")
    @ApiResponse(responseCode = "401", description = "UNAUTHENTICATED: missing, invalid or expired access token, or a refresh token",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "WORKOUT_NOT_FOUND: no workout with this id is owned by the caller, "
            + "or WORKOUT_EXERCISE_NOT_FOUND: the entry is not part of the workout",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> removeExercise(@PathVariable Long id, @PathVariable Long entryId) {
        workoutService.removeExercise(currentUser.currentUserId(), id, entryId);
        return ResponseEntity.noContent().build();
    }
}
