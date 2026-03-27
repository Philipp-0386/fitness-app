package de.phil.fitness.backend.signup.repository;


import de.phil.fitness.backend.signup.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *  Repository for {@link Role} persistence operations.
 */
public interface SignUpRoleRepository extends JpaRepository<Role, Long> {
}
