package com.lucaflix.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class UserDTO {

    @Data
    public static class UpdateUserRequest {
        @Size(min = 3, max = 20, message = "Username deve ter entre 3 e 20 caracteres")
        private String username;

        @Email(message = "Email deve ser válido")
        private String email;

        @Size(min = 3, max = 20, message = "Nome deve ter entre 3 e 20 caracteres")
        private String firstName;

        @Size(min = 3, max = 20, message = "Sobrenome deve ter entre 3 e 20 caracteres")
        private String lastName;
    }

    @Data
    public static class UserResponse {
        private String id;
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private String role;
        private Boolean isAccountEnabled;
        private Boolean isAccountLocked;
        private Boolean isCredentialsExpired;
        private Boolean isAccountExpired;
    }

    @Data
    public static class UserListResponse {
        private String id;
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private String role;
        private Boolean isAccountEnabled;
        private Boolean isAccountLocked;

        public UserListResponse(String string, String username, String firstName, String lastName, String email, String name, boolean accountNonExpired, boolean accountNonLocked) {
            this.id = string;
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.role = name;
            this.isAccountEnabled = accountNonExpired;
            this.isAccountLocked = accountNonLocked;
        }
    }

    @Data
    public static class PasswordVerificationRequest {
        private String password;
    }
}