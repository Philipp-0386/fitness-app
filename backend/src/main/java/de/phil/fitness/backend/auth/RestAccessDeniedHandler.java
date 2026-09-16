package de.phil.fitness.backend.auth;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Answers requests of authenticated users that the filter chain refused.
 *
 * Ownership checks inside services throw
 * {@link de.phil.fitness.backend.auth.exception.AccessDeniedException} instead, which the controller advice handles. Both paths produce the same response shape.
 */
@Component
@Slf4j
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final SecurityErrorWriter errorWriter;

    public RestAccessDeniedHandler(SecurityErrorWriter errorWriter) {
        this.errorWriter = errorWriter;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.warn("Access denied by filter chain. path={} {}", request.getRequestURI(), accessDeniedException.getMessage());
        errorWriter.write(
                request,
                response,
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED",
                "You do not have access to this resource");
    }
}
