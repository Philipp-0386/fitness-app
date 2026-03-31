package de.phil.fitness.backend.signup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.phil.fitness.backend.signup.model.User;

/**
 *  Repository for {@link User} persistence operations.
 */
public interface SignUpUserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
