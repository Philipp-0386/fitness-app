package de.phil.fitness.backend.login.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}
