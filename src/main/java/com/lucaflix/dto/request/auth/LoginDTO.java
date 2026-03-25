package com.lucaflix.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    @NotBlank(message = "Username or password required")
    private String login;

    @NotBlank(message = "Password required")
    private String password;
}
