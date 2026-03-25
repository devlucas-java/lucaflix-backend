package com.lucaflix.dto.response.auth;


import com.lucaflix.dto.response.user.UserDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtAuthDTO {
    private String accessToken;
    private String tokenType = "Bearer";
    private UserDTO user;
}
