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

    /// DELETA CONTA DO USUARIO ATUAL
    @DeleteMapping("/me")
    @Operation(summary = "Deletar conta", description = "Deleta a conta do usuário autenticado")
    public ResponseEntity<Map<String, String>> deleteCurrentUser(@CurrentUser User user) {
        try {
            userService.deleteUser(user.getId());
            return ResponseEntity.ok(Map.of("message", "Conta deletada com sucesso"));
        } catch (Exception e) {
            log.error("Erro ao deletar usuário: {}", user.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Falha ao deletar conta"));
        }
    }












    /// ENDPOINTS ADMINISTRATIVOS - REQUEREM PERMISSOES ESPECIAIS

    /// OBTEM USUARIO POR ID (APENAS ADMINS)
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Obter usuário por ID", description = "Retorna informações de um usuário específico (apenas para administradores)")
    public ResponseEntity<UserDTO.UserResponse> getUserById(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId) {
        try {
            User user = userService.getUserById(userId);
            UserDTO.UserResponse userResponse = userService.convertToUserResponse(user);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            log.error("Erro ao obter usuário por ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /// BLOQUEIA OU DESBLOQUEIA CONTA DO USUARIO (APENAS ADMINS)
    @PutMapping("/{userId}/lock")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Bloquear/Desbloquear usuário", description = "Bloqueia ou desbloqueia a conta de um usuário (apenas para administradores)")
    public ResponseEntity<Map<String, String>> setAccountLocked(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @RequestBody Map<String, Boolean> request) {
        try {
            boolean locked = request.getOrDefault("locked", false);
            userService.setAccountLocked(userId, locked);
            String action = locked ? "bloqueada" : "desbloqueada";
            return ResponseEntity.ok(Map.of("message", "Conta " + action + " com sucesso"));
        } catch (Exception e) {
            log.error("Erro ao alterar status de bloqueio do usuário {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Falha ao alterar status da conta"));
        }
    }

    /// HABILITA OU DESABILITA CONTA DO USUARIO (APENAS ADMINS)
    @PutMapping("/{userId}/enable")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @Operation(summary = "Habilitar/Desabilitar usuário", description = "Habilita ou desabilita a conta de um usuário (apenas para administradores)")
    public ResponseEntity<Map<String, String>> setAccountEnabled(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @RequestBody Map<String, Boolean> request) {
        try {
            boolean enabled = request.getOrDefault("enabled", true);
            userService.setAccountEnabled(userId, enabled);
            String action = enabled ? "habilitada" : "desabilitada";
            return ResponseEntity.ok(Map.of("message", "Conta " + action + " com sucesso"));
        } catch (Exception e) {
            log.error("Erro ao alterar status de habilitação do usuário {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Falha ao alterar status da conta"));
        }
    }

    /// PROMOVE USUARIO A ADMINISTRADOR (APENAS SUPER ADMINS)
    @PutMapping("/{userId}/promote-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Promover a administrador", description = "Promove um usuário a administrador (apenas para super administradores)")
    public ResponseEntity<Map<String, String>> promoteToAdmin(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer adminLevel = request.getOrDefault("adminLevel", 1);
            userService.promoteToAdmin(userId, adminLevel);
            return ResponseEntity.ok(Map.of("message", "Usuário promovido a administrador com sucesso"));
        } catch (Exception e) {
            log.error("Erro ao promover usuário {} a admin: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Falha ao promover usuário"));
        }
    }

    /// PROMOVE USUARIO A SUPER ADMINISTRADOR (APENAS SUPER ADMINS)
    @PutMapping("/{userId}/promote-super-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Promover a super administrador", description = "Promove um usuário a super administrador (apenas para super administradores)")
    public ResponseEntity<Map<String, String>> promoteToSuperAdmin(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId) {
        try {
            userService.promoteToSuperAdmin(userId);
            return ResponseEntity.ok(Map.of("message", "Usuário promovido a super administrador com sucesso"));
        } catch (Exception e) {
            log.error("Erro ao promover usuário {} a super admin: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Falha ao promover usuário"));
        }
    }

    /// REBAIXA USUARIO PARA USUARIO COMUM (APENAS SUPER ADMINS)
    @PutMapping("/{userId}/demote")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Rebaixar a usuário comum", description = "Rebaixa um usuário para usuário comum (apenas para super administradores)")
    public ResponseEntity<Map<String, String>> demoteToUser(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId) {
        try {
            userService.demoteToUser(userId);
            return ResponseEntity.ok(Map.of("message", "Usuário rebaixado para usuário comum com sucesso"));
        } catch (Exception e) {
            log.error("Erro ao rebaixar usuário {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Falha ao rebaixar usuário"));
        }
    }

    /// DELETA USUARIO ESPECIFICO (APENAS SUPER ADMINS)
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Deletar usuário", description = "Deleta um usuário específico (apenas para super administradores)")
    public ResponseEntity<Map<String, String>> deleteUser(
            @Parameter(description = "ID do usuário") @PathVariable UUID userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(Map.of("message", "Usuário deletado com sucesso"));
        } catch (Exception e) {
            log.error("Erro ao deletar usuário {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Falha ao deletar usuário"));
        }
    }
}