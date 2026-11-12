package com.billwise.web.dto.user;

import com.billwise.domain.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        String role,
        String preferredCurrency,
        boolean enabled,
        LocalDateTime createdAt) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getPreferredCurrency(),
                user.isEnabled(),
                user.getCreatedAt());
    }
}