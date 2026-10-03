package de.phil.fitness.backend.tokenRefresh.service;

import org.springframework.stereotype.Service;

import de.phil.fitness.backend.auth.JwtService;
import de.phil.fitness.backend.auth.exception.InvalidRefreshTokenException;
import de.phil.fitness.backend.tokenRefresh.dto.AccessRefreshRequest;
import de.phil.fitness.backend.tokenRefresh.dto.AccessRefreshResponse;
import de.phil.fitness.backend.user.service.UserService;

@Service
public class TokenRefreshService {

    private final JwtService jwtService;
    private final UserService userService;

    public TokenRefreshService(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    public AccessRefreshResponse refresh(AccessRefreshRequest request) {
        Long userId = jwtService.verifyRefreshToken(request.refreshToken());

        if (!userService.existsById(userId)) {
            throw new InvalidRefreshTokenException("Refresh token subject has no user. userId=" + userId);
        }

        return new AccessRefreshResponse(jwtService.generateAccessToken(userId.toString()));
    }
}
