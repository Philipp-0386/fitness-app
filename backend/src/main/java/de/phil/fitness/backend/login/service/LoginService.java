package de.phil.fitness.backend.login.service;

import de.phil.fitness.backend.login.dto.LoginRequest;
import de.phil.fitness.backend.login.dto.LoginResponse;
import de.phil.fitness.backend.user.model.User;
import de.phil.fitness.backend.user.service.UserService;
import de.phil.fitness.backend.auth.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginService(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userService.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if(!passwordEncoder.matches(request.password(), user.getPasswordHashed())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String accessToken = jwtService.generateAccessToken(user.getId().toString());
        String refreshToken = jwtService.generateRefreshToken(user.getId().toString());

        return new LoginResponse(accessToken, refreshToken);
    }
}
