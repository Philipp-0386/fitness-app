package de.phil.fitness.backend.signup.service;

import de.phil.fitness.backend.signup.dto.SignUpRequest;
import de.phil.fitness.backend.signup.dto.SignUpResponse;
import de.phil.fitness.backend.signup.exception.DefaultRoleNotFoundException;
import de.phil.fitness.backend.signup.exception.EmailAlreadyExistsException;
import de.phil.fitness.backend.signup.exception.UsernameAlreadyTaken;
import de.phil.fitness.backend.signup.mapper.SignUpMapper;
import de.phil.fitness.backend.user.model.Role;
import de.phil.fitness.backend.user.model.User;
import de.phil.fitness.backend.user.repository.RoleRepository;
import de.phil.fitness.backend.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Handles user creation process.
 */
@Service
@Transactional
@Slf4j
public class SignUpService {
    private final UserRepository UserRepository;
    private final RoleRepository RoleRepository;
    private final SignUpMapper signUpMapper;
    private final PasswordEncoder passwordEncoder;

    public SignUpService(UserRepository userRepo, RoleRepository roleRepo, SignUpMapper mapper, PasswordEncoder passwordEncoder) {
        this.UserRepository = userRepo;
        this.RoleRepository = roleRepo;
        this.signUpMapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new user after verifying parsed information.
     * @param dto containing the user's data, must not be {@code null}
     * @return returns a {@link SignUpResponse} object after successful creation
     */
    public SignUpResponse createUser(SignUpRequest dto) {
        log.debug("User creation initiated. email={}", dto.getEmail());
        if(UserRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "User with email: " +dto.getEmail()+ " already exists!");
        }
        if(UserRepository.existsByUsername(dto.getUsername())) {
            throw new UsernameAlreadyTaken(
                    "Username "+ dto.getUsername() +" already taken!"
            );
        }
        Role defaultRole = RoleRepository.findById(1L)
                .orElseThrow(() -> new DefaultRoleNotFoundException("Default role not found during user creation!"));
        User userEntity = signUpMapper.mapRequestToUserEntity(dto);
        userEntity.setRole(defaultRole);
        userEntity.setPasswordHashed(passwordEncoder.encode(dto.getPassword()));
        User savedUser = UserRepository.save(userEntity);
        log.info("User creation successful. userId={}, email={}", savedUser.getId(), savedUser.getEmail());
        return signUpMapper.mapUserEntityToResponse(savedUser);
    }
}
