package de.phil.fitness.backend.user.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * The annotated string must be an IANA region id known to {@link java.time.ZoneId}, e.g. {@code Europe/Berlin}.
 * {@code null} is valid, combine with {@code @NotBlank} to require a value.
 */
@Target({ElementType.FIELD, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimeZoneValidator.class)
public @interface ValidTimeZone {
    String message() default "Unknown time zone";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
