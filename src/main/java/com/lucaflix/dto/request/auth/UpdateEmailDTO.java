package com.lucaflix.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateEmailDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Email not valid")
    private String newEmail;

    @NotBlank(message = "Password is required")
    private String currentPassword;
}