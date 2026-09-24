package de.phil.fitness.backend.signup.service;

import de.phil.fitness.backend.signup.dto.SignUpRequest;
import de.phil.fitness.backend.signup.dto.SignUpResponse;
import de.phil.fitness.backend.signup.exception.EmailAlreadyExistsException;
import de.phil.fitness.backend.signup.exception.UsernameAlreadyTaken;
import de.phil.fitness.backend.signup.mapper.SignUpMapper;
import de.phil.fitness.backend.user.model.User;
import de.phil.fitness.backend.user.service.UserService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Handles user creation process.
 */
@Service
@Transactional
@Slf4j
public class SignUpService {
    private final UserService userService;
    private final SignUpMapper signUpMapper;

    public SignUpService(UserService userService, SignUpMapper mapper) {
        this.userService = userService;
        this.signUpMapper = mapper;
    }

    /**
     * Creates a new user after verifying parsed information.
     * @param dto containing the user's data, must not be {@code null}
     * @return returns a {@link SignUpResponse} object after successful creation
     */
    public SignUpResponse createUser(SignUpRequest dto) {
        log.debug("User creation initiated");
        if(userService.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException(
                    "User with this email already registered!");
        }
        if(userService.existsByUsername(dto.username())) {
            throw new UsernameAlreadyTaken(
                    "Username already taken!");
        }
        User userEntity = signUpMapper.mapRequestToUserEntity(dto);
        User savedUser = userService.createUser(userEntity, dto.password());
        log.info("User creation successful. userId={}", savedUser.getId());
        return signUpMapper.mapUserEntityToResponse(savedUser);
    }
}
