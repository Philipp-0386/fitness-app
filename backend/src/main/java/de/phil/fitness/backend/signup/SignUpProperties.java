package de.phil.fitness.backend.signup;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Relevant to toggle signup availability for prod.
 *
 * Binds {@code app.signup.*} config.
 * @param enabled whether new accounts may be created; defaults to {@code false} when unset
 */
@ConfigurationProperties(prefix = "app.signup")
public record SignUpProperties(
        boolean enabled
) {}
