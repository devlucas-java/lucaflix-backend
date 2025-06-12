package com.lucaflix.controller;

import com.lucaflix.dto.user.UserDTO;
import com.lucaflix.model.MinhaLista;
import com.lucaflix.model.User;
import com.lucaflix.security.CurrentUser;
import com.lucaflix.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "Endpoints de gerenciamento de usuários")
public class UserController {

    private final UserService userService;

    /// OBTEM INFORMACOES DO USUARIO ATUAL
    @GetMapping("/me")
    @Operation(summary = "Obter perfil do usuário", description = "Retorna as informações do usuário autenticado")
    public ResponseEntity<UserDTO.UserResponse> getCurrentUser(@CurrentUser User user) {
        try {
            UserDTO.UserResponse userResponse = userService.convertToUserResponse(user);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            log.error("Erro ao obter informações do usuário: {}", user.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /// ATUALIZA DADOS DO USUARIO ATUAL
    @PutMapping("/me")
    @Operation(summary = "Atualizar perfil", description = "Atualiza as informações do usuário autenticado")
    public ResponseEntity<Map<String, Object>> updateCurrentUser(
            @Valid @RequestBody UserDTO.UpdateUserRequest request,
            @CurrentUser User user) {
        try {
            User updatedUser = userService.updateUser(user, request);
            UserDTO.UserResponse userResponse = userService.convertToUserResponse(updatedUser);

            return ResponseEntity.ok(Map.of(
                    "message", "Usuário atualizado com sucesso",
                    "user", userResponse
            ));
        } catch (IllegalArgumentException e) {
            log.warn("Atualização de usuário falhou para {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erro inesperado durante atualização do usuário: {}", user.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Falha ao atualizar usuário"));
        }
    }

    /// DELETA CONTA DO USUARIO ATUAL - CORRIGIDO PARA EVITAR DADOS ÓRFÃOS
    @DeleteMapping("/me")
    @Operation(summary = "Deletar conta", description = "Deleta a conta do usuário autenticado e todos os dados relacionados, mantendo as mídias")
    public ResponseEntity<Map<String, String>> deleteCurrentUser(@CurrentUser User user) {
        try {
            // Chama o serviço que fará a exclusão segura
            userService.deleteUserAndRelatedData(user.getId());

            log.info("Usuário {} deletado com sucesso junto com todos os dados relacionados", user.getUsername());
            return ResponseEntity.ok(Map.of("message", "Conta e todos os dados relacionados foram deletados com sucesso"));

        } catch (Exception e) {
            log.error("Erro ao deletar usuário {}: {}", user.getUsername(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Falha ao deletar conta. Tente novamente mais tarde."));
        }
    }
}