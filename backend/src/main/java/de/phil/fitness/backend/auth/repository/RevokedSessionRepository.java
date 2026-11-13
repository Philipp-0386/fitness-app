package de.phil.fitness.backend.auth.repository;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import de.phil.fitness.backend.auth.model.RevokedSession;

public interface RevokedSessionRepository extends JpaRepository<RevokedSession, UUID> {
    void deleteByExpiresAtBefore(Instant now);
}
