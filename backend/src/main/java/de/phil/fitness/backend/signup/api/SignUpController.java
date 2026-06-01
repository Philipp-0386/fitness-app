package de.phil.fitness.backend.signup.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.phil.fitness.backend.signup.dto.SignUpRequest;
import de.phil.fitness.backend.signup.dto.SignUpResponse;
import de.phil.fitness.backend.signup.service.SignUpService;

/**
 * REST endpoint responsible for handling user creation process.
 */
@RestController
@RequestMapping("/backend/auth")
public class SignUpController {
    private final SignUpService signUpService;

    public SignUpController(SignUpService sus) {
        this.signUpService = sus;
    }

    /**
     * Default endpoint for user creation.
     * @param request Contains user information after automatically mapped by Jackson
     * @return Returns {@link SignUpResponse} object after successful creation process
     */
    @PostMapping("/signup")
    //Frontend hinweis: yyyy-MM-dd für dateOfBirth angeben (input date type)
    public SignUpResponse root(@RequestBody SignUpRequest request) {
        return signUpService.createUser(request);
    }
}
