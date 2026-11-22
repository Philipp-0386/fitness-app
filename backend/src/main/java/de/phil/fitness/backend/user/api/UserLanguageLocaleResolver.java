package de.phil.fitness.backend.user.api;

import java.util.Locale;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;

import de.phil.fitness.backend.auth.CurrentUser;
import de.phil.fitness.backend.user.repository.UserSettingsRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Resolves the request locale from the language in the settings of the authenticated user, read once per request.
 * Unauthenticated requests are English. Code reads the result through {@code LocaleContextHolder.getLocale()}.
 */
@Component(DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)
public class UserLanguageLocaleResolver implements LocaleResolver {

    private static final String ATTRIBUTE = UserLanguageLocaleResolver.class.getName() + ".locale";

    private final CurrentUser currentUser;
    private final UserSettingsRepository userSettingsRepository;

    public UserLanguageLocaleResolver(CurrentUser currentUser, UserSettingsRepository userSettingsRepository) {
        this.currentUser = currentUser;
        this.userSettingsRepository = userSettingsRepository;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        if (request.getAttribute(ATTRIBUTE) instanceof Locale cached) {
            return cached;
        }
        Locale locale = Locale.ENGLISH;
        if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken) {
            locale = userSettingsRepository.findById(currentUser.currentUserId())
                    .map(settings -> settings.getLanguage().getLocale())
                    .orElse(Locale.ENGLISH);
        }
        request.setAttribute(ATTRIBUTE, locale);
        return locale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        throw new UnsupportedOperationException("The language is changed through the user settings");
    }
}
