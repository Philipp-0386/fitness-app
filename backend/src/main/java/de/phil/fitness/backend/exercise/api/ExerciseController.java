package de.phil.fitness.backend.exercise.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.phil.fitness.backend.auth.CurrentUser;
import de.phil.fitness.backend.exercise.dto.ExerciseResponse;
import de.phil.fitness.backend.exercise.service.ExerciseService;

/**
 * Controller showing the exercise catalog.
 */
@RestController
@RequestMapping("/backend/exercises")
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
    public ResponseEntity<List<ExerciseResponse>> getExercises() {
        return ResponseEntity.ok(exerciseService.findAvailable(currentUser.currentUserId()));
    }
}
