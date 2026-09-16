package de.phil.fitness.backend.exercise.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.phil.fitness.backend.exercise.dto.ExerciseResponse;
import de.phil.fitness.backend.exercise.mapper.ExerciseMapper;
import de.phil.fitness.backend.exercise.repository.ExerciseRepository;

/**
 * Read access to the exercise catalog.
 */
@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;

    public ExerciseService(ExerciseRepository exerciseRepository,
                           ExerciseMapper exerciseMapper) {
        this.exerciseRepository = exerciseRepository;
        this.exerciseMapper = exerciseMapper;
    }

    /**
     * Lists every exercise a user may pick from.
     * @param userId id of the user the catalog is assembled for
     * @return global catalog entries merged with that user's own definitions
     */
    @Transactional(readOnly = true)
    public List<ExerciseResponse> findAvailable(Long userId) {
        return exerciseRepository.findAvailableTo(userId)
                .stream()
                .map(exerciseMapper::toResponse)
                .toList();
    }
}
