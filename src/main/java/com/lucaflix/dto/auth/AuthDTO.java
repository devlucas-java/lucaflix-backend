package com.lucaflix.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthDTO {

    @Data
    public static class LoginRequest {
        @NotBlank(message = "Username ou email é obrigatório")
        private String usernameOrEmail;

        @NotBlank(message = "Senha é obrigatória")
        private String password;
    }

    @Data
    public static class SignUpRequest {
        @NotBlank(message = "Nome completo é obrigatório")
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        private String firstName;

        @NotBlank(message = "Nome completo é obrigatório")
        @Size(min = 2, max = 100, message = "Sobrenome deve ter entre 2 e 100 caracteres")
        private String lastName;

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ser válido")
        private String email;

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8, message = "Senha deve ter pelo menos 8 caracteres")
        private String password;
    }

    @Data
    public static class JwtAuthResponse {
        private String accessToken;
        private String tokenType = "Bearer";
        private UserResponse user;

        public JwtAuthResponse(String accessToken, UserResponse user) {
            this.accessToken = accessToken;
            this.user = user;
        }
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
        private AdminPanelInfo adminPanel;

        @Data
        public static class AdminPanelInfo {
            private String id;
            private Integer adminLevel;
        }
    }

    @Data
    public static class PasswordChangeRequest {
        @NotBlank(message = "Senha atual é obrigatória")
        private String currentPassword;

        @NotBlank(message = "Nova senha é obrigatória")
        @Size(min = 8, message = "Senha deve ter pelo menos 8 caracteres")
        private String newPassword;
    }

    @Data
    public static class EmailUpdateRequest {
        @NotBlank(message = "Novo email é obrigatório")
        @Email(message = "Email deve ser válido")
        private String newEmail;

        @NotBlank(message = "Senha atual é obrigatória para verificação")
        private String currentPassword;
    }
}