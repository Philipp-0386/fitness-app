package de.phil.fitness.backend.smoketest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TestUserRepository extends JpaRepository<TestUser, Integer> {

    Optional<TestUser> findByUsername(String username);

    Optional<TestUser> findById(Long id);

    List<TestUser> findByDateOfBirthBefore(LocalDate date);
}
