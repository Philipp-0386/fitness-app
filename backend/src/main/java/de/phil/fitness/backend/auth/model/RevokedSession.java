package de.phil.fitness.backend.auth.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "REVOKED_SESSION")
@Getter
public class RevokedSession {
    @Id
    @Column(name = "SESSION_ID")
    private UUID sessionId;

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "EXPIRES_AT", nullable = false)
    private Instant expiresAt;

    protected RevokedSession() {

    }

    public RevokedSession(UUID sessionId, Long userId, Instant expiresAt) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.expiresAt = expiresAt;
    }
}
