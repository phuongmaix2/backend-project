package com.billwise.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTests {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void convertsResourceNotFoundExceptionToProblemDetail() {
        ResourceNotFoundException exception = new ResourceNotFoundException("User not found");

        ProblemDetail problem = handler.handleResourceNotFound(exception);

        assertThat(problem.getStatus())
                .isEqualTo(HttpStatus.NOT_FOUND.value());

        assertThat(problem.getTitle())
                .isEqualTo("Resource not found");

        assertThat(problem.getDetail())
                .isEqualTo("User not found");
    }

    @Test
    void convertsConflictExceptionToProblemDetail() {
        ConflictException exception = new ConflictException("Email already exists");

        ProblemDetail problem = handler.handleConflict(exception);

        assertThat(problem.getStatus())
                .isEqualTo(HttpStatus.CONFLICT.value());

        assertThat(problem.getTitle())
                .isEqualTo("Data conflict");

        assertThat(problem.getDetail())
                .isEqualTo("Email already exists");
    }
}