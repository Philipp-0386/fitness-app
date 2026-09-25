package de.phil.fitness.backend.user.service;

import de.phil.fitness.backend.user.dto.UserDeleteRequest;
import de.phil.fitness.backend.user.dto.UserResponse;
import de.phil.fitness.backend.user.exception.DefaultRoleNotFoundException;
import de.phil.fitness.backend.user.exception.InvalidPasswordException;
import de.phil.fitness.backend.user.exception.UserNotFoundException;
import de.phil.fitness.backend.user.mapper.UserMapper;
import de.phil.fitness.backend.user.model.Role;
import de.phil.fitness.backend.user.model.User;
import de.phil.fitness.backend.user.repository.RoleRepository;
import de.phil.fitness.backend.user.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Encapsulates the logic of the user domain, including the account endpoints of the authenticated user.
 * Other services must use this class instead of accessing the user repositories directly.
 */
@Service
@Transactional
@Slf4j
public class UserService {
    private static final long DEFAULT_ROLE_ID = 1L;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                       EntityManager entityManager, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
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

    private User loadUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No user with id=" + userId));
    }

    /**
     * @param userId id of the authenticated user
     * @return the user as returned to the client
     * @throws UserNotFoundException if the user no longer exists
     */
    public UserResponse getUser(Long userId) {
        User user = loadUser(userId);
        return userMapper.toResponse(user);
    }

    /**
     * Deletes the user and, through {@code ON DELETE CASCADE}, everything the user owns.
     *
     * <p>The references between owned rows are {@code DEFERRABLE}; checked immediately they fire
     * while the cascade has only removed part of the rows. Deferring them to commit lets the
     * cascade finish first, and a reference from a row that survives still fails at commit.
     * @param userId id of the authenticated user
     * @param req the password confirming the deletion
     * @throws UserNotFoundException if the user no longer exists
     * @throws InvalidPasswordException if the password does not match
     */
    public void deleteUser(Long userId, UserDeleteRequest req) {
        User user = loadUser(userId);
        if (!passwordEncoder.matches(req.password(), user.getPasswordHashed())) {
            throw new InvalidPasswordException("Password confirmation failed for userId=" + userId);
        }
        entityManager.createNativeQuery("SET CONSTRAINTS ALL DEFERRED").executeUpdate();
        userRepository.delete(user);
        log.info("User deleted. userId={}", userId);
    }
}
