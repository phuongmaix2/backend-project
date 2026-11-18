package com.billwise.service;

import com.billwise.domain.User;
import com.billwise.exception.ConflictException;
import com.billwise.repository.UserRepository;
import com.billwise.web.dto.auth.RegisterRequest;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/** Registers users, normalizes identity fields, and stores only encoded passwords. */
@Service
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegisterRequest request) {
        String normalizedUsername = request.username().trim().toLowerCase(Locale.ROOT);

        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new ConflictException(
                    "Username already exists");
        }

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException(
                    "Email already exists");
        }

        User user = new User(
                normalizedUsername,
                normalizedEmail,
                passwordEncoder.encode(request.password()));

        user.setPreferredCurrency(request.preferredCurrency());

        return userRepository.save(user);
    }
}
