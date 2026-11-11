package com.billwise.exception;

// Thrown when an external service cannot complete a request.
public class ExternalServiceUnavailableException extends RuntimeException {

    public ExternalServiceUnavailableException(String message) {
        super(message);
    }

    public ExternalServiceUnavailableException(
            String message,
            Throwable cause) {
        super(message, cause);
    }
}