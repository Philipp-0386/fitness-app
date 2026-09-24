package de.phil.fitness.backend.signup.mapper;

import org.springframework.stereotype.Component;

import de.phil.fitness.backend.signup.dto.SignUpRequest;
import de.phil.fitness.backend.signup.dto.SignUpResponse;
import de.phil.fitness.backend.user.model.User;

/**
 * This mapper class converts numerous objects during the user creation process.
 */
@Component
public class SignUpMapper {
    public SignUpMapper() {

    }

    /**
     * Converts {@link SignUpRequest} object into {@link User} entity.
     * @param sur  {@link SignUpRequest} object to convert, must not be {@code null}
     * @return  returns a {@link User} entity populated with the user's data
     */
    public User mapRequestToUserEntity(SignUpRequest sur) {
        User user = new User();
        user.setUsername(sur.username());
        user.setEmail(sur.email());
        return user;
    }

    /**
     * Converts {@link User} entity into {@link SignUpResponse} object.
     * @param user the {@link User} entity to convert, must not be {@code null}
     * @return returns a {@link SignUpResponse} object populated with the user's data
     */
    public SignUpResponse mapUserEntityToResponse(User user) {
        SignUpResponse res = new SignUpResponse();
        res.setUsername(user.getUsername());
        res.setEmail(user.getEmail());
        return res;
    }
}
