package de.phil.fitness.backend.program.service;

import de.phil.fitness.backend.program.dto.CreateProgramRequest;
import de.phil.fitness.backend.program.dto.ProgramDetailResponse;
import de.phil.fitness.backend.program.dto.ProgramResponse;
import de.phil.fitness.backend.program.exception.PresetProgramNotFoundException;
import de.phil.fitness.backend.program.exception.ProgramLimitReachedException;
import de.phil.fitness.backend.program.exception.UserProgramNotFoundException;
import de.phil.fitness.backend.program.mapper.ProgramMapper;
import de.phil.fitness.backend.program.model.Program;
import de.phil.fitness.backend.program.model.ProgramDay;
import de.phil.fitness.backend.program.repository.ProgramRepository;
import de.phil.fitness.backend.routine.service.RoutineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProgramService {
    private final ProgramRepository programRepository;
    private final ProgramMapper programMapper;
    private final RoutineService routineService;

    public ProgramService(ProgramRepository programRepository, ProgramMapper programMapper, RoutineService routineService) {
        this.programRepository = programRepository;
        this.programMapper = programMapper;
        this.routineService = routineService;
    }

    /**
     * Lists all user-made programs.
     * @param userId Id of the requesting user.
     * @return List of user-owned programs.
     */
    @Transactional(readOnly = true)
    public List<ProgramResponse> findUserPrograms(Long userId) {
        return programRepository.getAllUserProgramsByUserId(userId)
                .stream()
                .map(programMapper::toProgramResponse)
                .toList();
    }

    /**
     * Retrieves single user-owned program including its days.
     * @param userId Id of the requesting user.
     * @param programId Id of the searched program.
     * @return Singular user-owned program with its days.
     * @throws UserProgramNotFoundException if no program with this id is owned by the user
     */
    @Transactional(readOnly = true)
    public ProgramDetailResponse findDetailedUserProgram(Long userId, Long programId) {
        Program program = programRepository.getUserProgramDetailedById(userId, programId)
                .orElseThrow(() -> new UserProgramNotFoundException("The user-program with id " + programId + " does not exist"));
        Set<Long> routineIds = program.getDays().stream()
                .map(ProgramDay::getRoutineId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return programMapper.toProgramDetailResponse(program, routineService.findSummariesByRoutineId(routineIds));
    }

    /**
     * Retrieves the active program of a user including its days.
     * @param userId Id of the requesting user.
     * @return The active program with its days, empty if the user has none.
     */
    @Transactional(readOnly = true)
    public Optional<ProgramDetailResponse> findActiveUserProgram(Long userId) {
        Optional<Program> program = programRepository.getActiveUserProgram(userId);
        if (program.isEmpty()) {
            return Optional.empty();
        }
        Set<Long> routineIds = program.get().getDays().stream()
                .map(ProgramDay::getRoutineId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return Optional.of(programMapper.toProgramDetailResponse(program.get(), routineService.findSummariesByRoutineId(routineIds)));
    }

    /**
     * Lists all preset-programs.
     * @return List of all preset-programs.
     */
    @Transactional(readOnly = true)
    public List<ProgramResponse> findPresetPrograms() {
        return programRepository.getAllPresetPrograms()
                .stream()
                .map(programMapper::toProgramResponse)
                .toList();
    }

    /**
     * Retrieves single preset-program including its days.
     * @param programId Id of the searched program.
     * @return Singular preset-program with its days.
     * @throws PresetProgramNotFoundException if no preset-program with this id exists
     */
    @Transactional(readOnly = true)
    public ProgramDetailResponse findDetailedPresetProgram(Long programId) {
        Program program = programRepository.getPresetProgramDetailedById(programId)
                .orElseThrow(() -> new PresetProgramNotFoundException("The preset-program with id " + programId + " does not exist"));
        Set<Long> routineIds = program.getDays().stream()
                .map(ProgramDay::getRoutineId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return programMapper.toProgramDetailResponse(program, routineService.findSummariesByRoutineId(routineIds));
    }

    /**
     * Creates a program owned by the requesting user. The first program of a user becomes active.
     * @param request The submitted program, the order of its days is taken from the list.
     * @param userId Id of the user the program is created for.
     * @return The created program with its days.
     * @throws ProgramLimitReachedException if the user already owns the maximum number of programs
     * @throws de.phil.fitness.backend.routine.exception.UserRoutineNotFoundException if a referenced routine is not
     *         owned by the user
     */
    @Transactional
    public ProgramDetailResponse createNewUserProgram(CreateProgramRequest request, Long userId) {
        long count = programRepository.countByUserId(userId);
        if(count>=5) {
            throw new ProgramLimitReachedException("The user " + userId + " already owns " + count + " programs");
        }

        Program program = programMapper.toProgramEntity(request, userId);
        program.setActive(count == 0);

        Set<Long> routineIds = program.getDays().stream()
                .map(ProgramDay::getRoutineId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        routineService.idOwnershipCheck(userId, routineIds);

        return programMapper.toProgramDetailResponse(programRepository.save(program), routineService.findSummariesByRoutineId(routineIds));
    }

    /**
     * Deletes a user-owned program including its days. Workouts keep existing without the reference to their day.
     * Deleting the active program leaves the user without an active program.
     * @param userId Id of the requesting user.
     * @param programId Id of the program to delete.
     * @throws UserProgramNotFoundException if no program with this id is owned by the user
     */
    @Transactional
    public void deleteUserProgram(Long userId, Long programId) {
        Program program = programRepository.getUserProgramDetailedById(userId, programId)
                .orElseThrow(() -> new UserProgramNotFoundException("The user-program with id " + programId + " does not exist"));
        programRepository.delete(program);
    }
}
