package de.phil.fitness.backend.tokenRefresh.dto;

import jakarta.validation.constraints.NotBlank;

public record AccessRefreshRequest(
        @NotBlank String refreshToken
) {}
