package de.phil.fitness.backend.smoketest;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoInterface extends JpaRepository<UserData, Integer> {
}
