package de.phil.fitness.backend.routine.api;

import de.phil.fitness.backend.auth.CurrentUser;
import de.phil.fitness.backend.routine.dto.RoutineResponse;
import de.phil.fitness.backend.routine.service.RoutineService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/backend/routines")
public class RoutineController {
    private final RoutineService routineService;
    private final CurrentUser currentUser;

    public RoutineController(RoutineService routineService,  CurrentUser currentUser) {
        this.routineService = routineService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public List<RoutineResponse> getUserRoutines() {
        return routineService.findUserRoutines(currentUser.currentUserId());
    }

    @GetMapping("/{id}")
    public RoutineResponse getUserRoutine(@PathVariable Long id) {
        return routineService.findSpecificUserRoutine(currentUser.currentUserId(), id);
    }

    @GetMapping("/presets")
    public List<RoutineResponse> getPresetRoutines() {
        return routineService.findPresetRoutines();
    }

    @GetMapping("/presets/{id}")
    public RoutineResponse getPresetRoutine(@PathVariable Long id) {
        return routineService.findSpecificPresetRoutine(id);
    }
}
