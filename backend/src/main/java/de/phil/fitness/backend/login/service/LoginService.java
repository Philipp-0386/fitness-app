package de.phil.fitness.backend.login.service;

import de.phil.fitness.backend.login.dto.LoginRequest;
import de.phil.fitness.backend.login.dto.LoginResponse;
import de.phil.fitness.backend.login.exception.TooManyLoginAttemptsException;
import de.phil.fitness.backend.user.model.User;
import de.phil.fitness.backend.user.service.UserService;
import de.phil.fitness.backend.auth.JwtService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoginService {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /** Failed attempts per username. the window starts with the first failure */
    private final Cache<String, AtomicInteger> failedAttempts = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(15))
            .maximumSize(10_000)
            .build();

    public LoginService(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        String username = request.username();

        AtomicInteger failures = failedAttempts.getIfPresent(username);
        if (failures != null && failures.get() >= MAX_FAILED_ATTEMPTS) {
            throw new TooManyLoginAttemptsException("Login blocked after repeated failures. username=" + username);
        }

        Optional<User> user = userService.findByUsername(username);
        if (user.isEmpty() || !passwordEncoder.matches(request.password(), user.get().getPasswordHashed())) {
            failedAttempts.get(username, key -> new AtomicInteger()).incrementAndGet();
            throw new BadCredentialsException("Invalid credentials");
        }

        failedAttempts.invalidate(username);

        String accessToken = jwtService.generateAccessToken(user.get().getId().toString());
        String refreshToken = jwtService.generateRefreshToken(user.get().getId().toString());

        return new LoginResponse(accessToken, refreshToken);
    }
}
