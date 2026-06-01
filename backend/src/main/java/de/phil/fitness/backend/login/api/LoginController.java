package de.phil.fitness.backend.login.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.phil.fitness.backend.login.dto.LoginRequest;
import de.phil.fitness.backend.login.dto.LoginResponse;
import de.phil.fitness.backend.login.service.LoginService;
/**
 * Controller responsible for handling login requests.
 */
@RestController
@RequestMapping("/backend/auth")
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> handleLoginRequest(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = loginService.login(request);
        return ResponseEntity.ok(response);
    }
}
