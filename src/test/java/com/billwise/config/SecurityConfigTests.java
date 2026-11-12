package com.billwise.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SecurityConfigTests {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void encodesAndMatchesPassword() {
        String plainPassword = "DemoPassword123!";

        String passwordHash = passwordEncoder.encode(plainPassword);

        assertThat(passwordHash)
                .isNotEqualTo(plainPassword);

        assertThat(passwordEncoder.matches(
                plainPassword,
                passwordHash)).isTrue();

        assertThat(passwordEncoder.matches(
                "WrongPassword123!",
                passwordHash)).isFalse();
    }
}