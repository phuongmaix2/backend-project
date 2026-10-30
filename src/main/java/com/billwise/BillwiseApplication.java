
package com.billwise;

import com.billwise.domain.User;
import com.billwise.repository.UserRepository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

@SpringBootApplication
public class BillwiseApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillwiseApplication.class, args);
    }

    // Adds a demo user when the application starts.
    @Bean
    public CommandLineRunner initializeDB(UserRepository userRepository) {
        return args -> {

            // Prevents duplicate demo users.
            if (!userRepository.existsByUsername("demo")) {

                PasswordEncoder encoder =
                    PasswordEncoderFactories.createDelegatingPasswordEncoder();

                User user = new User(
                    "demo",
                    "demo@example.com",
                    encoder.encode("DemoPassword123!")
                );

                userRepository.save(user);
                System.out.println("Demo user created!");
            }
        };
    }
}
