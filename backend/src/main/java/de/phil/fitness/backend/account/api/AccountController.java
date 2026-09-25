package de.phil.fitness.backend.account.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.phil.fitness.backend.account.dto.AccountDeleteRequest;
import de.phil.fitness.backend.account.dto.AccountResponse;
import de.phil.fitness.backend.account.service.AccountService;
import de.phil.fitness.backend.auth.CurrentUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Account of the authenticated user. There is no id in the path, the user is always the token subject.
 */
@RestController
@RequestMapping("/backend/me")
@Tag(name = "Account", description = "Account of the authenticated user")
public class AccountController {

    private final AccountService accountService;
    private final CurrentUser currentUser;

    public AccountController(AccountService accountService, CurrentUser currentUser) {
        this.accountService = accountService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public ResponseEntity<AccountResponse> getAccount() {
        return ResponseEntity.ok(accountService.getAccount(currentUser.currentUserId()));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(@Valid @RequestBody AccountDeleteRequest req) {
        accountService.deleteAccount(currentUser.currentUserId(), req);
        return ResponseEntity.noContent().build();
    }
}
