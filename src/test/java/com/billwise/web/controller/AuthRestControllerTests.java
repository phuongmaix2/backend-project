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

import com.billwise.repository.UserRepository;
import com.billwise.service.UserLoginService;
import com.billwise.web.dto.auth.LoginRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.context.SecurityContextRepository;

import java.util.Optional;

class AuthRestControllerTests {

    private UserRegistrationService registrationService;
    private MockMvc mockMvc;
    private UserLoginService loginService;
    private UserRepository userRepository;
    private SecurityContextRepository securityContextRepository;

    @BeforeEach
    void setUp() {
        registrationService = mock(UserRegistrationService.class);
        loginService = mock(UserLoginService.class);
        userRepository = mock(UserRepository.class);
        securityContextRepository = mock(SecurityContextRepository.class);

        AuthRestController controller = new AuthRestController(
                registrationService,
                loginService,
                userRepository,
                securityContextRepository);

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

    @Test
    void logsInUserAndReturnsUserResponse()
            throws Exception {

        Authentication authentication = mock(Authentication.class);

        User user = new User(
                "alice",
                "alice@example.com",
                "encoded-password");

        user.setPreferredCurrency("EUR");

        when(loginService.authenticate(
                any(LoginRequest.class))).thenReturn(authentication);

        when(authentication.getName())
                .thenReturn("alice");

        when(userRepository.findByUsername("alice"))
                .thenReturn(Optional.of(user));

        String requestJson = """
                {
                  "username": "alice",
                  "password": "Test1234!"
                }
                """;

        mockMvc.perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.email")
                        .value("alice@example.com"))
                .andExpect(jsonPath("$.passwordHash")
                        .doesNotExist());

        verify(loginService)
                .authenticate(any(LoginRequest.class));
    }
}