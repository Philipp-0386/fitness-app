package de.phil.fitness.backend.smoketest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Simple test routing ignoring all architecture.
 */
@RestController
@RequestMapping("/api/test")
@Slf4j
public class ControllerServiceTest {
    private final RepoInterface ri;

    public ControllerServiceTest(RepoInterface ri) {
        this.ri = ri;
    }

    @GetMapping
    public String rootResponse() {
        log.info("root called");
        return "Test";
    }
    @GetMapping("/db")
    public List<UserData> getAllUsers() {
        log.info("getAllUsers called");
        return ri.findAll();
    }
}
