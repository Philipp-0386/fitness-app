package de.phil.fitness.backend.user.model;

import java.util.Locale;

import lombok.Getter;

@Getter
public enum Language {
    EN(Locale.ENGLISH),
    DE(Locale.GERMAN);

    private final Locale locale;

    Language(Locale locale) {
        this.locale = locale;
    }
}
