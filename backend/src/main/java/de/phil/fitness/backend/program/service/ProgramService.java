package de.phil.fitness.backend.program.service;

import de.phil.fitness.backend.program.dto.CreateProgramRequest;
import de.phil.fitness.backend.program.dto.ProgramDetailResponse;
import de.phil.fitness.backend.program.dto.ProgramResponse;
import de.phil.fitness.backend.program.dto.UpdateProgramDayRequest;
import de.phil.fitness.backend.program.dto.UpdateProgramRequest;
import de.phil.fitness.backend.program.exception.PresetProgramNotFoundException;
import de.phil.fitness.backend.program.exception.ProgramDayNotFoundException;
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
     * Updates a user-owned program including its days. Days sent with their id keep it, days without an id are added,
     * and days missing from the request are removed.
     * @param userId Id of the requesting user.
     * @param programId Id of the program to update.
     * @param request The complete new state of the program, the order of its days is taken from the list.
     * @return The updated program with its days.
     * @throws UserProgramNotFoundException if no program with this id is owned by the user
     * @throws ProgramDayNotFoundException if a day id does not belong to this program
     * @throws de.phil.fitness.backend.routine.exception.UserRoutineNotFoundException if a referenced routine is not
     *         owned by the user
     */
    @Transactional
    public ProgramDetailResponse updateUserProgram(Long userId, Long programId, UpdateProgramRequest request) {
        Program program = programRepository.getUserProgramDetailedById(userId, programId)
                .orElseThrow(() -> new UserProgramNotFoundException("The user-program with id " + programId + " does not exist"));

        Set<Long> dayIds = program.getDays().stream()
                .map(ProgramDay::getId)
                .collect(Collectors.toSet());
        request.days().stream()
                .map(UpdateProgramDayRequest::id)
                .filter(id -> id != null && !dayIds.contains(id))
                .findFirst()
                .ifPresent(id -> {
                    throw new ProgramDayNotFoundException("The day with id " + id + " does not belong to program " + programId);
                });

        Set<Long> routineIds = request.days().stream()
                .map(UpdateProgramDayRequest::routineId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        routineService.idOwnershipCheck(userId, routineIds);

        programMapper.applyUpdateOfUserProgram(program, request);
        return programMapper.toProgramDetailResponse(programRepository.saveAndFlush(program), routineService.findSummariesByRoutineId(routineIds));
    }

    /**
     * Makes a user-owned program the active one and deactivates the previously active program.
     * @param userId Id of the requesting user.
     * @param programId Id of the program to activate.
     * @return The now active program with its days.
     * @throws UserProgramNotFoundException if no program with this id is owned by the user
     */
    @Transactional
    public ProgramDetailResponse activateUserProgram(Long userId, Long programId) {
        if (!programRepository.existsByIdAndUserId(programId, userId)) {
            throw new UserProgramNotFoundException("The user-program with id " + programId + " does not exist");
        }
        programRepository.deactivateUserPrograms(userId);
        programRepository.activateUserProgram(userId, programId);
        return findDetailedUserProgram(userId, programId);
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
