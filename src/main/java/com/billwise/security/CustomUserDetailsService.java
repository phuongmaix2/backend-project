package com.billwise.security;

import com.billwise.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/** Adapts BillWise users to Spring Security's authentication model. */

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        String normalizedUsername = username.trim().toLowerCase(Locale.ROOT);

        com.billwise.domain.User user = userRepository
                .findByUsername(normalizedUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .roles(user.getRole())
                .disabled(!user.isEnabled())
                .build();
    }

}
