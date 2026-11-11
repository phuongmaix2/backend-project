package com.billwise.exception;

// Thrown when the current user is not allowed to perform an operation.
public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException(String message) {
        super(message);
    }
}