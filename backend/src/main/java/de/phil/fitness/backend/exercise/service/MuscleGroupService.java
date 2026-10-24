package de.phil.fitness.backend.exercise.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.phil.fitness.backend.exercise.dto.MuscleAreaResponse;
import de.phil.fitness.backend.exercise.dto.MuscleGroupResponse;
import de.phil.fitness.backend.exercise.model.MuscleGroup;
import de.phil.fitness.backend.exercise.repository.MuscleGroupRepository;

/**
 * Read access to the muscle group lookup.
 */
@Service
public class MuscleGroupService {

    private final MuscleGroupRepository muscleGroupRepository;

    public MuscleGroupService(MuscleGroupRepository muscleGroupRepository) {
        this.muscleGroupRepository = muscleGroupRepository;
    }

    /**
     * Lists the muscle groups as a two level tree. Groups and areas come from one query, so reading an area's parent
     * id needs no further one.
     * @return the top level groups, each with its areas
     */
    @Transactional(readOnly = true)
    public List<MuscleGroupResponse> findAll() {
        List<MuscleGroup> muscleGroups = muscleGroupRepository.findAllByOrderByIdAsc();

        Map<Long, List<MuscleAreaResponse>> areasByGroupId = muscleGroups.stream()
                .filter(muscleGroup -> muscleGroup.getParent() != null)
                .collect(Collectors.groupingBy(
                        area -> area.getParent().getId(),
                        Collectors.mapping(area -> new MuscleAreaResponse(area.getId(), area.getName()), Collectors.toList())));

        return muscleGroups.stream()
                .filter(muscleGroup -> muscleGroup.getParent() == null)
                .map(group -> new MuscleGroupResponse(
                        group.getId(),
                        group.getName(),
                        group.getBodyRegion(),
                        areasByGroupId.getOrDefault(group.getId(), List.of())))
                .toList();
    }
}
