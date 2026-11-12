package com.billwise.web.controller;

import com.billwise.domain.User;
import com.billwise.exception.ApiExceptionHandler;
import com.billwise.service.UserRegistrationService;
import com.billwise.web.dto.auth.RegisterRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthRestControllerTests {

    private UserRegistrationService registrationService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        registrationService = mock(UserRegistrationService.class);

        AuthRestController controller = new AuthRestController(registrationService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void registersUserAndReturnsCreatedResponse()
            throws Exception {

        User savedUser = new User(
                "mai",
                "mai@example.com",
                "encoded-password");

        savedUser.setPreferredCurrency("EUR");

        when(registrationService.register(
                any(RegisterRequest.class))).thenReturn(savedUser);

        String requestJson = """
                {
                  "username": "mai",
                  "email": "mai@example.com",
                  "password": "StrongPass123!",
                  "preferredCurrency": "EUR"
                }
                """;

        mockMvc.perform(
                post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("mai"))
                .andExpect(
                        jsonPath("$.email").value("mai@example.com"))
                .andExpect(
                        jsonPath("$.preferredCurrency").value("EUR"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        verify(registrationService)
                .register(any(RegisterRequest.class));
    }

    @Test
    void rejectsInvalidRegistrationRequest()
            throws Exception {

        String requestJson = """
                {
                  "username": "",
                  "email": "not-an-email",
                  "password": "weak",
                  "preferredCurrency": "eur"
                }
                """;

        mockMvc.perform(
                post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.username").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.password").exists())
                .andExpect(
                        jsonPath("$.errors.preferredCurrency").exists());

        verifyNoInteractions(registrationService);
    }
}