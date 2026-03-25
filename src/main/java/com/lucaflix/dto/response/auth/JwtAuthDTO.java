package com.lucaflix.dto.response.auth;


import com.lucaflix.dto.request.user.UpdateUserDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtAuthDTO {
    private String accessToken;
    private String tokenType = "Bearer";
    private UpdateUserDTO user;
}
