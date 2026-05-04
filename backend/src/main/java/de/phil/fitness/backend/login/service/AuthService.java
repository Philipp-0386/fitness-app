package de.phil.fitness.backend.login.service;

import de.phil.fitness.backend.login.dto.LoginRequest;
import de.phil.fitness.backend.signup.model.User;
import de.phil.fitness.backend.signup.repository.SignUpUserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final SignUpUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(SignUpUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHashed())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return user;
    }
}
