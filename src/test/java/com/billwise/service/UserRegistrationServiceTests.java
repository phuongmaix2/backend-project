package com.billwise.service;

import com.billwise.domain.User;
import com.billwise.exception.ConflictException;
import com.billwise.repository.UserRepository;
import com.billwise.web.dto.auth.RegisterRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserRegistrationService registrationService;

    @Test
    void registersUserWithNormalizedDataAndEncodedPassword() {
        RegisterRequest request = new RegisterRequest(
                "  Mai  ",
                "  MAI@EXAMPLE.COM  ",
                "StrongPass123!",
                "EUR");

        when(passwordEncoder.encode("StrongPass123!"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User user = registrationService.register(request);

        assertThat(user.getUsername()).isEqualTo("mai");
        assertThat(user.getEmail()).isEqualTo("mai@example.com");
        assertThat(user.getPasswordHash())
                .isEqualTo("encoded-password");
        assertThat(user.getPreferredCurrency()).isEqualTo("EUR");

        verify(passwordEncoder).encode("StrongPass123!");
        verify(userRepository).save(user);
    }

    @Test
    void rejectsDuplicateUsername() {
        RegisterRequest request = new RegisterRequest(
                "mai",
                "mai@example.com",
                "StrongPass123!",
                "EUR");

        when(userRepository.existsByUsername("mai"))
                .thenReturn(true);

        assertThatThrownBy(
                () -> registrationService.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Username already exists");

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void rejectsDuplicateEmail() {
        RegisterRequest request = new RegisterRequest(
                "mai",
                "mai@example.com",
                "StrongPass123!",
                "EUR");

        when(userRepository.existsByEmail("mai@example.com"))
                .thenReturn(true);

        assertThatThrownBy(
                () -> registrationService.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Email already exists");

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }
}