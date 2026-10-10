package de.phil.fitness.backend.common;

import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * Base class of every exception that maps to an {@link ErrorResponse}
 * */
@Getter
public abstract class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final String clientMessage;
    private final Map<String, String> fieldErrors;

    /**
     * @param status HTTP status of the response
     * @param code internal code evaluated by the frontend
     * @param clientMessage message sent to the client
     * @param logMessage message written to the log, may contain internal details such as ids
     */
    protected ApiException(HttpStatus status, String code, String clientMessage, String logMessage) {
        this(status, code, clientMessage, logMessage, null);
    }

    /**
     * @param status HTTP status of the response
     * @param code internal code evaluated by the frontend
     * @param clientMessage message sent to the client
     * @param logMessage message written to the log, may contain internal details such as ids
     * @param fieldErrors rejected fields mapped to their message, sent to the client
     */
    protected ApiException(HttpStatus status, String code, String clientMessage, String logMessage,
                           Map<String, String> fieldErrors) {
        super(logMessage);
        this.status = status;
        this.code = code;
        this.clientMessage = clientMessage;
        this.fieldErrors = fieldErrors;
    }
}
