package de.phil.fitness.backend.user.mapper;

import de.phil.fitness.backend.user.dto.UserResponse;
import de.phil.fitness.backend.user.dto.UserSettingsResponse;
import de.phil.fitness.backend.user.model.User;
import de.phil.fitness.backend.user.model.UserSettings;
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

    public UserSettingsResponse toSettingsResponse(UserSettings userSettings) {
        return new UserSettingsResponse(
                userSettings.getWeightUnit(),
                userSettings.getDistanceUnit(),
                userSettings.getTimeZone()
        );
    }
}
