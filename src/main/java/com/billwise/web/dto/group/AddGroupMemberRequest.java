package com.billwise.web.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AddGroupMemberRequest {

    @NotBlank(message = "Username or email is required")
    @Size(max = 254, message = "Username or email is too long")
    private String usernameOrEmail;

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(
            String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }
}