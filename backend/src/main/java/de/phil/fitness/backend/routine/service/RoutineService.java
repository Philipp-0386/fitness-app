package de.phil.fitness.backend.routine.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.phil.fitness.backend.routine.dto.RoutinePresetResponse;
import de.phil.fitness.backend.routine.mapper.RoutineMapper;
import de.phil.fitness.backend.routine.repository.RoutineRepository;

@Service
public class RoutineService {
    private final RoutineRepository routineRepository;
    private final RoutineMapper routineMapper;

    public RoutineService(RoutineRepository routineRepository, RoutineMapper routineMapper) {
        this.routineRepository = routineRepository;
        this.routineMapper = routineMapper;
    }

    /**
     * Lists all preset routines defined by the system as defaults to access for all users. Throws exception on empty list.
     * @return 
     */
    @Transactional(readOnly = true)
    public List<RoutinePresetResponse> findPresetRoutines() {
        return routineRepository.getAllPresetRoutines()
                .stream()
                .map(routineMapper::toRoutinePresetResponse)
                .toList();
    }
}
