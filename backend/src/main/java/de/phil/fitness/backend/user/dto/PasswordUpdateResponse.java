package de.phil.fitness.backend.user.dto;

public record PasswordUpdateResponse(
        String accessToken,
        String refreshToken
) {}
