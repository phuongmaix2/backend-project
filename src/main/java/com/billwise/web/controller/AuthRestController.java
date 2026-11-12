package com.billwise.web.controller;

import com.billwise.domain.User;
import com.billwise.service.UserRegistrationService;
import com.billwise.web.dto.auth.RegisterRequest;
import com.billwise.web.dto.user.UserResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthRestController {

    private final UserRegistrationService registrationService;

    public AuthRestController(
        UserRegistrationService registrationService
    ) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(
        @Valid @RequestBody RegisterRequest request
    ) {
        User user = registrationService.register(request);

        return UserResponse.from(user);
    }
}