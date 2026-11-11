package com.billwise.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Converts exceptions from REST controllers into consistent JSON responses
@RestControllerAdvice(annotations = RestController.class)
public class ApiExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResouceNotFound(ResourceNotFoundException exception) {
        return createProblem(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                exception.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException exception) {
        return createProblem(
                HttpStatus.CONFLICT,
                "Data conflict",
                exception.getMessage());
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ProblemDetail handleForbiddenOperation(ForbiddenOperationException exception) {
        return createProblem(
                HttpStatus.FORBIDDEN,
                "Operation forbidden",
                exception.getMessage());
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ProblemDetail handleBussinessRule(BusinessRuleException exception) {
        return createProblem(
                HttpStatus.BAD_REQUEST,
                "Business rule violation",
                exception.getMessage());
    }

    @ExceptionHandler(ExternalServiceUnavailableException.class)
    public ProblemDetail handleExternalServiceUnavailable(ExternalServiceUnavailableException exception) {
        return createProblem(
                HttpStatus.SERVICE_UNAVAILABLE,
                "External service unavailable",
                exception.getMessage());
    }

    private ProblemDetail createProblem(
            HttpStatus status,
            String title,
            String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                status,
                detail);

        problem.setTitle(title);
        return problem;
    }

}
