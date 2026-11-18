package com.billwise;

import com.billwise.domain.User;
import com.billwise.repository.UserRepository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BillwiseApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillwiseApplication.class, args);
    }

    // Adds a demo user when the application starts.
    @Bean
    @Profile("!test")
    public CommandLineRunner initializeDB(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {

            // Prevents duplicate demo users.
            if (!userRepository.existsByUsername("demo")) {

                User user = new User(
                        "demo",
                        "demo@example.com",
                        passwordEncoder.encode("DemoPassword123!"));

                userRepository.save(user);
                System.out.println("Demo user created!");
            }
        };
    }
}
