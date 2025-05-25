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
    }

    @Data
    public static class UserResponse {
        private String id;
        private String username;
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
        private String email;
        private String role;
        private Boolean isAccountEnabled;
        private Boolean isAccountLocked;
    }

    @Data
    public static class WatchedMovieRequest {
        private Long filmeId;
    }

    @Data
    public static class WatchingSeriesRequest {
        private Long serieId;
    }

    @Data
    public static class WatchedEpisodeRequest {
        private Long episodioId;
    }

    @Data
    public static class PasswordVerificationRequest {
        private String password;
    }
}