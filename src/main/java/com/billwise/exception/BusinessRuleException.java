package com.billwise.exception;

// Thrown when a request violates a BillWise business rule.
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}