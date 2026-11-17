package com.billwise.web.controller;

import com.billwise.domain.User;
import com.billwise.repository.UserRepository;
import com.billwise.service.UserLoginService;
import com.billwise.service.UserRegistrationService;
import com.billwise.web.dto.auth.LoginRequest;
import com.billwise.web.dto.auth.RegisterRequest;
import com.billwise.web.dto.user.UserResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {

    private final UserRegistrationService registrationService;
    private final UserLoginService loginService;
    private final UserRepository userRepository;
    private final SecurityContextRepository securityContextRepository;

    public AuthRestController(
            UserRegistrationService registrationService,
            UserLoginService loginService,
            UserRepository userRepository,
            SecurityContextRepository securityContextRepository) {

        this.registrationService = registrationService;
        this.loginService = loginService;
        this.userRepository = userRepository;
        this.securityContextRepository = securityContextRepository;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public UserResponse register(
            @Valid @RequestBody RegisterRequest request) {

        User user = registrationService.register(request);

        return UserResponse.from(user);
    }

    @PostMapping("/login")
    public UserResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Authentication authentication = loginService.authenticate(request);

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
                context,
                httpRequest,
                httpResponse);

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow();

        return UserResponse.from(user);
    }
}