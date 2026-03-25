package com.lucaflix.dto.request.auth;

import jakarta.validation.constraints.NotBlank;

public class LoginDTO {
    @NotBlank(message = "Username or password required")
    private String usernameOrEmail;

    @NotBlank(message = "Password required")
    private String password;
}
