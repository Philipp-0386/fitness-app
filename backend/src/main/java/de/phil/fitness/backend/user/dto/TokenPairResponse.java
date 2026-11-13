package de.phil.fitness.backend.user.dto;

public record TokenPairResponse(
        String accessToken,
        String refreshToken
) {}
