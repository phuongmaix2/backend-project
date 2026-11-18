package com.billwise.web.dto.auth;

import com.billwise.validation.ValidCurrency;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Form-backing object for the browser registration page. */
public class RegistrationForm {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must contain 3 to 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username contains unsupported characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 254, message = "Email must not exceed 254 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 12, max = 72, message = "Password must contain 12 to 72 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)"
            + "(?=.*[^a-zA-Z0-9]).+$", message = "Password must contain uppercase, lowercase, "
                    + "number and special characters")
    private String password;

    @NotBlank(message = "Preferred currency is required")
    @ValidCurrency
    private String preferredCurrency;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPreferredCurrency() {
        return preferredCurrency;
    }

    public void setPreferredCurrency(
            String preferredCurrency) {
        this.preferredCurrency = preferredCurrency;
    }

    public RegisterRequest toRegisterRequest() {
        return new RegisterRequest(
                username,
                email,
                password,
                preferredCurrency);
    }
}
