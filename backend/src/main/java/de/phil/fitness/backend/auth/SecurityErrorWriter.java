package de.phil.fitness.backend.auth;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import tools.jackson.databind.ObjectMapper;

import de.phil.fitness.backend.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Writes an {@link ErrorResponse} body for failures raised inside the security filter chain.
 *
 * <p>Those failures never reach the controller advice, so without this the client would receive an
 * empty 401 or 403. Shares the response shape the advice produces.
 */
@Component
public class SecurityErrorWriter {

    private final ObjectMapper objectMapper;

    public SecurityErrorWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Writes the given error to the response, unless the response has already been committed.
     * @param request the request that failed, used for the reported path
     * @param response the response to write the error body to
     * @param status the HTTP status to send
     * @param code the application error code evaluated by the frontend
     * @param message the message sent to the client
     * @throws IOException if the body cannot be written
     */
    public void write(HttpServletRequest request,
                      HttpServletResponse response,
                      HttpStatus status,
                      String code,
                      String message) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(
                response.getOutputStream(),
                new ErrorResponse(code, message, request.getRequestURI()));
    }
}
