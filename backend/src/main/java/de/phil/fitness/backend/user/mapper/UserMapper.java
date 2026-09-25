package de.phil.fitness.backend.user.mapper;

import de.phil.fitness.backend.user.dto.UserResponse;
import de.phil.fitness.backend.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
