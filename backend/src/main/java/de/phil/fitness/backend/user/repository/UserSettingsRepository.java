package de.phil.fitness.backend.user.repository;

import de.phil.fitness.backend.user.model.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
}
