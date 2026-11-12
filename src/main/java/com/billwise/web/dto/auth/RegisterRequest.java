package com.billwise.web.dto.auth;

import com.billwise.validation.ValidCurrency;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Username is required") @Size(min = 3, max = 50, message = "Username must contain 3 to 50 characters") @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username contains unsupported characters") String username,

        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") @Size(max = 254, message = "Email must not exceed 254 characters") String email,

        @NotBlank(message = "Password is required") @Size(min = 12, max = 72, message = "Password must contain 12 to 72 characters") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)"
                + "(?=.*[^a-zA-Z0-9]).+$", message = "Password must contain uppercase, lowercase, "
                        + "number and special characters") String password,

        @NotBlank(message = "Preferred currency is required") @ValidCurrency String preferredCurrency

) {
}