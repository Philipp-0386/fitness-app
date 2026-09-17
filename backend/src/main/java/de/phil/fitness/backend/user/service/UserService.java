package de.phil.fitness.backend.user.service;

import de.phil.fitness.backend.user.exception.DefaultRoleNotFoundException;
import de.phil.fitness.backend.user.model.Role;
import de.phil.fitness.backend.user.model.User;
import de.phil.fitness.backend.user.repository.RoleRepository;
import de.phil.fitness.backend.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Encapsulates all persistence-related logic of the user domain.
 * Other services must use this class instead of accessing the user repositories directly.
 */
@Service
@Transactional
public class UserService {
    private static final long DEFAULT_ROLE_ID = 1L;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @param email email to check, must not be {@code null}
     * @return {@code true} if a user with this email already exists
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * @param username username to check, must not be {@code null}
     * @return {@code true} if a user with this username already exists
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * @param username username to look up, must not be {@code null}
     * @return the matching user, or an empty {@link Optional} if none exists
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Persists a new user with the default role and a hashed password.
     * @param user the user entity to persist, must not be {@code null}
     * @param rawPassword the plain text password, which gets hashed before saving
     * @return the saved {@link User}
     * @throws DefaultRoleNotFoundException if the default role does not exist
     */
    public User createUser(User user, String rawPassword) {
        Role defaultRole = roleRepository.findById(DEFAULT_ROLE_ID)
                .orElseThrow(() -> new DefaultRoleNotFoundException("Default role not found during user creation!"));
        user.setRole(defaultRole);
        user.setPasswordHashed(passwordEncoder.encode(rawPassword));
        return userRepository.save(user);
    }
}
