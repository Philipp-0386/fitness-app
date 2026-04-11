package de.phil.fitness.backend.login.api;

import de.phil.fitness.backend.login.dto.LoginResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint responsible for logins.
 */
@RestController
@RequestMapping("/backend/login")
public class LoginController {
    /**
     * Default endpoint for login requests.
     * @return
     */
    @PostMapping
    public LoginResponse handleLoginRequest() {
        return new LoginResponse();
    }
}
