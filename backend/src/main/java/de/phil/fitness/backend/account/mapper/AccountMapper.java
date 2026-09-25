package de.phil.fitness.backend.account.mapper;

import org.springframework.stereotype.Component;

import de.phil.fitness.backend.account.dto.AccountResponse;
import de.phil.fitness.backend.user.model.User;

@Component
public class AccountMapper {

    public AccountResponse toResponse(User user) {
        return new AccountResponse(user.getUsername(), user.getEmail(), user.getCreatedAt());
    }
}
