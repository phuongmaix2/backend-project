package com.billwise.security;

import com.billwise.domain.User;
import com.billwise.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUpUser() {
        userRepository.deleteAll();

        User user = new User(
                "phuong",
                "phuong@example.com",
                passwordEncoder.encode("StrongPass123!"));

        userRepository.save(user);
    }

    @Test
    void redirectsUnauthenticatedUserToLogin()
            throws Exception {

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void logsInWithCorrectCredentials()
            throws Exception {

        mockMvc.perform(
                formLogin()
                        .user("phuong")
                        .password("StrongPass123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(
                        authenticated().withUsername("phuong"));
    }

    @Test
    void rejectsIncorrectPassword()
            throws Exception {

        mockMvc.perform(
                formLogin()
                        .user("phuong")
                        .password("WrongPassword123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    void logsOutAndInvalidatesSession()
            throws Exception {

        MvcResult loginResult = mockMvc.perform(
                formLogin()
                        .user("phuong")
                        .password("StrongPass123!"))
                .andExpect(authenticated())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult
                .getRequest()
                .getSession(false);

        mockMvc.perform(
                post("/logout")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"))
                .andExpect(unauthenticated());
    }
}