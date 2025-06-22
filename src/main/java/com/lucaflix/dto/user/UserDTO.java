package com.lucaflix.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

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
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserListResponse {
        private String id;
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private String role;
        private Boolean isAccountEnabled;
        private Boolean isAccountLocked;

        // Construtor customizado para manter compatibilidade com SuperAdminService
        public UserListResponse(String id, String username, String firstName, String lastName,
                                String email, String role, boolean isAccountEnabled, boolean isAccountLocked) {
            this.id = id;
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.role = role;
            this.isAccountEnabled = isAccountEnabled;
            this.isAccountLocked = !isAccountLocked; // Inverte porque User.isAccountNonLocked() retorna o oposto de isAccountLocked
        }
    }

    @Data
    public static class PasswordVerificationRequest {
        private String password;
    }
}