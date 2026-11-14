package com.billwise.config;

import com.billwise.domain.User;
import com.billwise.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class LocalDataInitializer
        implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LocalDataInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createUserIfMissing(
                "alice",
                "alice@example.com",
                "Test1234!");

        createUserIfMissing(
                "bob",
                "bob@example.com",
                "Test1234!");

        createUserIfMissing(
                "charlie",
                "charlie@example.com",
                "Test1234!");
    }

    private void createUserIfMissing(
            String username,
            String email,
            String password) {

        boolean userAlreadyExists = userRepository.existsByUsername(username)
                || userRepository.existsByEmail(email);

        if (userAlreadyExists) {
            return;
        }

        User user = new User(
                username,
                email,
                passwordEncoder.encode(password));

        userRepository.save(user);
    }
}