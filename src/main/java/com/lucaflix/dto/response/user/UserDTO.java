package com.lucaflix.dto.response.user;

import lombok.Data;

@Data
public class UserDTO {
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