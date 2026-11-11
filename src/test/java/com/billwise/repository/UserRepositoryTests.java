package com.billwise.repository;

import com.billwise.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void savesUserWithNormalizedEmailAndDefaultValues() {
        User user = new User(
                "mai",
                "  MAI@EXAMPLE.COM  ",
                "encoded-password");

        userRepository.saveAndFlush(user);

        User savedUser = userRepository.findByUsername("mai")
                .orElseThrow();

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("mai@example.com");
        assertThat(savedUser.getPasswordHash()).isEqualTo("encoded-password");
        assertThat(savedUser.getRole()).isEqualTo("USER");
        assertThat(savedUser.getPreferredCurrency()).isEqualTo("EUR");
        assertThat(savedUser.isEnabled()).isTrue();
        assertThat(savedUser.getCreatedAt()).isNotNull();
    }
}