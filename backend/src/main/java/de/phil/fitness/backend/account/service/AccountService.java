package de.phil.fitness.backend.account.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.phil.fitness.backend.account.dto.AccountDeleteRequest;
import de.phil.fitness.backend.account.dto.AccountResponse;
import de.phil.fitness.backend.account.exception.InvalidPasswordException;
import de.phil.fitness.backend.account.mapper.AccountMapper;
import de.phil.fitness.backend.user.exception.UserNotFoundException;
import de.phil.fitness.backend.user.model.User;
import de.phil.fitness.backend.user.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * Reads and deletes the account of the authenticated user.
 */
@Service
@Slf4j
public class AccountService {

    private final UserService userService;
    private final AccountMapper accountMapper;

    public AccountService(UserService userService, AccountMapper accountMapper) {
        this.userService = userService;
        this.accountMapper = accountMapper;
    }

    /**
     * @param userId id of the authenticated user
     * @return the user's account
     * @throws UserNotFoundException if the user no longer exists
     */
    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long userId) {
        return accountMapper.toResponse(loadUser(userId));
    }

    /**
     * Deletes the account and all data owned by it.
     * @param userId id of the authenticated user
     * @param req the password confirming the deletion
     * @throws InvalidPasswordException if the password does not match
     */
    @Transactional
    public void deleteAccount(Long userId, AccountDeleteRequest req) {
        User user = loadUser(userId);
        requirePassword(user, req.password());
        userService.deleteUser(user);
        log.info("Account deleted. userId={}", userId);
    }

    private User loadUser(Long userId) {
        return userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No user with id=" + userId));
    }

    private void requirePassword(User user, String rawPassword) {
        if (!userService.passwordMatches(user, rawPassword)) {
            throw new InvalidPasswordException("Password confirmation failed for userId=" + user.getId());
        }
    }
}
