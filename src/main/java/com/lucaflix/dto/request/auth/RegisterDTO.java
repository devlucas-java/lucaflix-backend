package com.lucaflix.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterDTO {
    @NotBlank(message = "First Name required")
    @Size(min = 2, max = 100, message = "The name must be between 2 and 100 characters ")
    private String firstName;

    @NotBlank(message = "Last Name is requires")
    @Size(min = 2, max = 100, message = "The last name must be between 2 and 100 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "The email not valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 50, message = "The password must be between 8 and 50 characters")
    private String password;
}
