package de.phil.fitness.backend.testrouting;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backend/test")
public class TestController {

    @GetMapping
    public String test() {
        return "Success!";
    }
}
