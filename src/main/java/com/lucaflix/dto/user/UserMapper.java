package com.lucaflix.dto.user;

import com.lucaflix.dto.auth.AuthDTO;
import com.lucaflix.dto.user.UserDTO;
import com.lucaflix.model.AdminPanel;
import com.lucaflix.model.User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserMapper {

    /// CONVERTE USER ENTITY PARA USER RESPONSE DTO
    public UserDTO.UserResponse toUserResponse(User user) {
        UserDTO.UserResponse response = new UserDTO.UserResponse();
        response.setId(user.getId().toString());
        response.setUsername(user.getUsername());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setIsAccountEnabled(user.getIsAccountEnabled());
        response.setIsAccountLocked(user.getIsAccountLocked());
        response.setIsCredentialsExpired(user.getIsCredentialsExpired());
        response.setIsAccountExpired(user.getIsAccountExpired());
        return response;
    }

    /// CONVERTE USER ENTITY PARA AUTH USER RESPONSE DTO (USADO NO LOGIN E REGISTER)
    public AuthDTO.UserResponse toAuthUserResponse(User user, Optional<AdminPanel> adminPanel) {
        AuthDTO.UserResponse userResponse = new AuthDTO.UserResponse();
        userResponse.setId(user.getId().toString());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole().name());
        userResponse.setIsAccountEnabled(user.getIsAccountEnabled());
        userResponse.setIsAccountLocked(user.getIsAccountLocked());
        userResponse.setIsCredentialsExpired(user.getIsCredentialsExpired());
        userResponse.setIsAccountExpired(user.getIsAccountExpired());

        /// ADICIONA INFORMACOES DO PAINEL ADMIN SE EXISTIR
        if (adminPanel.isPresent()) {
            AuthDTO.UserResponse.AdminPanelInfo adminPanelInfo = new AuthDTO.UserResponse.AdminPanelInfo();
            adminPanelInfo.setId(adminPanel.get().getId().toString());
            adminPanelInfo.setAdminLevel(adminPanel.get().getAdminLevel());
            userResponse.setAdminPanel(adminPanelInfo);
        }

        return userResponse;
    }
}