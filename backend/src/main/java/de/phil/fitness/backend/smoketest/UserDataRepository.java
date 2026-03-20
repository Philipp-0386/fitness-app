package de.phil.fitness.backend.smoketest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserDataRepository extends JpaRepository<UserData, Integer> {

    Optional<UserData> findByUsername(String username);

    Optional<UserData> findById(Long id);

    List<UserData> findByDateOfBirthBefore(LocalDate date);
}
