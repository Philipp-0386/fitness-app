package de.phil.fitness.backend.login.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.phil.fitness.backend.config.JwtService;
import de.phil.fitness.backend.login.dto.LoginRequest;
import de.phil.fitness.backend.login.dto.LoginResponse;
import de.phil.fitness.backend.login.service.AuthService;
import de.phil.fitness.backend.signup.model.User;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/backend/login")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @Value("${jwt.expiration:900000}")
    private long jwtExpiration;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }
    
    @PostMapping
    public LoginResponse handleLoginRequest(@RequestBody LoginRequest request, HttpServletResponse response) {
        User user = authService.login(request);
        String token = jwtService.generate(user);

        long maxAgeSeconds = jwtExpiration / 1000;
        response.setHeader("Set-Cookie",
                "jwt=" + token + "; HttpOnly; Secure; Path=/; Max-Age=" + maxAgeSeconds + "; SameSite=Strict");

        return new LoginResponse(user.getUsername(), user.getRole().getName());
    }
}
