package de.phil.fitness.backend.signup.service;

import de.phil.fitness.backend.signup.dto.SignUpRequest;
import de.phil.fitness.backend.signup.dto.SignUpResponse;
import de.phil.fitness.backend.signup.exception.DefaultRoleNotFoundException;
import de.phil.fitness.backend.signup.exception.EmailAlreadyExistsException;
import de.phil.fitness.backend.signup.mapper.SignUpMapper;
import de.phil.fitness.backend.signup.model.Role;
import de.phil.fitness.backend.signup.model.User;
import de.phil.fitness.backend.signup.repository.SignUpRoleRepository;
import de.phil.fitness.backend.signup.repository.SignUpUserRepository;

import java.util.Optional;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Handles user creation process.
 */
@Service
@Transactional
public class SignUpService {
    private final SignUpUserRepository signUpUserRepository;
    private final SignUpRoleRepository signUpRoleRepository;
    private final SignUpMapper signUpMapper;
    private final PasswordEncoder passwordEncoder;

    public SignUpService(SignUpUserRepository suur, SignUpRoleRepository surr, SignUpMapper sum, PasswordEncoder pwe) {
        this.signUpUserRepository = suur;
        this.signUpRoleRepository = surr;
        this.signUpMapper = sum;
        this.passwordEncoder = pwe;
    }

    /**
     * Creates a new user after verifying parsed information.
     * @param dto containing the user's data, must not be {@code null}
     * @return returns a {@link SignUpResponse} object after successful creation.
     */

    public SignUpResponse createUser(SignUpRequest dto) {
        if(signUpUserRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "User with email: " +dto.getEmail()+ " already exists!");
        }
        Role defaultRole = signUpRoleRepository.findById(1L)
                .orElseThrow(() -> new DefaultRoleNotFoundException("Default role not found"));

        User userEntity = signUpMapper.mapRequestToUserEntity(dto);
        userEntity.setRole(defaultRole);
        userEntity.setPasswordHashed(passwordEncoder.encode(dto.getPasswordUnhashed()));
        User savedUser = signUpUserRepository.save(userEntity);

        return signUpMapper.mapUserEntityToResponse(savedUser);
    }
}
