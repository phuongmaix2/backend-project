package com.billwise.service;

import com.billwise.web.dto.auth.LoginRequest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Locale;

/** Keeps authentication orchestration out of the REST and MVC controllers. */
@Service
public class UserLoginService {

    private final AuthenticationManager authenticationManager;

    public UserLoginService(
            AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /** Delegates password verification to Spring Security's configured provider. */
    public Authentication authenticate(LoginRequest request) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));
    }
}
