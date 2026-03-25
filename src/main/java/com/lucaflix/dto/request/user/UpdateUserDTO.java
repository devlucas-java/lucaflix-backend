package com.lucaflix.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateUserDTO {
        @Size(min = 3, max = 20, message = "The username must be between 3 and 20 characters")
        private String username;

        @Email(message = "Email not valid")
        private String email;

        @Size(min = 2, max = 50, message = "The name must be between 2 and 50 characters")
        private String firstName;

        @Size(min = 2, max = 50, message = "The last name must be between 2 and 50 characters")
        private String lastName;

}