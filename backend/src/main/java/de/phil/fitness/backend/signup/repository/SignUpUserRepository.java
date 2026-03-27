package de.phil.fitness.backend.signup.repository;

import de.phil.fitness.backend.signup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *  Repository for {@link User} persistence operations.
 */
public interface SignUpUserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

}
