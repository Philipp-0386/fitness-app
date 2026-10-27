package de.phil.fitness.backend.program.api;

import de.phil.fitness.backend.auth.CurrentUser;
import de.phil.fitness.backend.common.ErrorResponse;
import de.phil.fitness.backend.program.dto.CreateProgramRequest;
import de.phil.fitness.backend.program.dto.ProgramDetailResponse;
import de.phil.fitness.backend.program.dto.ProgramResponse;
import de.phil.fitness.backend.program.service.ProgramService;
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
 * Controller for the programs of the authenticated user and the preset programs available to everyone.
 */
@RestController
@RequestMapping("/backend/programs")
@Tag(name = "Programs", description = "Own programs of the authenticated user and system preset programs")
public class ProgramController {
    private final CurrentUser currentUser;
    private final ProgramService programService;

    public ProgramController(CurrentUser currentUser, ProgramService programService) {
        this.currentUser = currentUser;
        this.programService =  programService;
    }

    @GetMapping()
    public List<ProgramResponse> getUserPrograms() {
        return programService.findUserPrograms(currentUser.currentUserId());
    }

    @GetMapping("/{id}")
    public ProgramDetailResponse getUserProgram(@PathVariable Long id) {
        return programService.findDetailedUserProgram(currentUser.currentUserId(), id);
    }

    @GetMapping("/presets")
    public List<ProgramResponse> getPresetPrograms() {
        return programService.findPresetPrograms();
    }

    @GetMapping("/presets/{id}")
    public ProgramDetailResponse getPresetProgram(@PathVariable Long id) {
        return programService.findDetailedPresetProgram(id);
    }

    @GetMapping("/active")
    public ResponseEntity<ProgramDetailResponse> getActiveUserProgram() {
        return programService.findActiveUserProgram(currentUser.currentUserId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping()
    public ResponseEntity<ProgramDetailResponse> createNewProgram(@Valid @RequestBody CreateProgramRequest request) {
        ProgramDetailResponse created = programService.createNewUserProgram(request, currentUser.currentUserId());
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/backend/programs/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
}
