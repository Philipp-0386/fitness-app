package de.phil.fitness.backend.user.repository;

import de.phil.fitness.backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 *  Repository for {@link User} persistence operations.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByUsernameIgnoreCase(String username);
    Optional<User> findByUsernameIgnoreCase(String username);
}
