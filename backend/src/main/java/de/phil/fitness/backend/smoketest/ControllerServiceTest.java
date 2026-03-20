package de.phil.fitness.backend.smoketest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Simple test routing ignoring all architecture.
 */
@RestController
@RequestMapping("/backend/test")
@Slf4j
public class ControllerServiceTest {
    private final UserDataRepository userRepo;
    private final RoleRepository roleRepo;

    public ControllerServiceTest(UserDataRepository userRepo, RoleRepository roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    @GetMapping
    public String rootResponse() {
        log.info("root called");
        return "Test";
    }
    @GetMapping("/users")
    public List<UserData> getAllUsers() {
        log.info("getAllUsers called");
        return userRepo.findAll();
    }
    @GetMapping("/user/{id}")
    public Optional<UserData> getUserById(@PathVariable Long id) {
        log.info("Specific user request ({})", id);
        return userRepo.findById(id);
    }
    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        log.info("getAllRoles called");
        return roleRepo.findAll();
    }
}
