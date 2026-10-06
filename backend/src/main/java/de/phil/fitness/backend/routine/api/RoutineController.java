package de.phil.fitness.backend.routine.api;

import de.phil.fitness.backend.routine.dto.RoutinePresetResponse;
import de.phil.fitness.backend.routine.service.RoutineService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/backend/routines")
public class RoutineController {
    private final RoutineService routineService;

    public RoutineController(RoutineService routineService) {
        this.routineService = routineService;
    }

    @GetMapping("/presets")
    public List<RoutinePresetResponse> getRoutinePresets() {
        return routineService.findPresetRoutines();
    }
}
