package de.phil.fitness.backend.user.repository;


import de.phil.fitness.backend.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 *  Repository for {@link Role} persistence operations.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findById(long id);
}
