package com.billwise.exception;

// Thrown when a requested database resource does not exist.
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}