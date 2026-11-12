package com.billwise.security;

import com.billwise.domain.User;
import com.billwise.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTests {

    @Mock
    private UserRepository userRepository;

    @Test
    void loadsUserByNormalizedUsername() {
        User user = new User(
                "mai",
                "mai@example.com",
                "encoded-password");

        when(userRepository.findByUsername("mai"))
                .thenReturn(Optional.of(user));

        CustomUserDetailsService service = new CustomUserDetailsService(userRepository);

        UserDetails userDetails = service.loadUserByUsername("  Mai  ");

        assertThat(userDetails.getUsername()).isEqualTo("mai");
        assertThat(userDetails.getPassword())
                .isEqualTo("encoded-password");
        assertThat(userDetails.isEnabled()).isTrue();

        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    void rejectsUnknownUsername() {
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        CustomUserDetailsService service = new CustomUserDetailsService(userRepository);

        assertThatThrownBy(
                () -> service.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Invalid username or password");
    }
}