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
    void savesAndFindsUserByUsername() {
        User user = new User(
                "mai",
                "mai@example.com",
                "encoded-password");

        userRepository.save(user);

        assertThat(userRepository.findByUsername("mai"))
                .isPresent();
    }
}