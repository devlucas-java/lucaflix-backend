package com.lucaflix.controller;

import com.lucaflix.dto.media.PaginatedResponseDTO;
import com.lucaflix.dto.user.UserDTO;
import com.lucaflix.model.User;
import com.lucaflix.security.CurrentUser;
import com.lucaflix.service.SuperAdminService;
import com.lucaflix.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.UUID;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

    private final SuperAdminService superAdminService;
    private final UserService userService;



    @GetMapping("/search")
    public ResponseEntity<PaginatedResponseDTO<UserDTO.UserListResponse>> searchUsers(
            @RequestParam(required = false) String searchTerm,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(superAdminService.searchUsers(searchTerm, pageable));
    }


    /**
     * FUNCAO DE FAZER UPGRADE
     * SE FOR USER VAI PARA ADMIN
     * SE FOR ADMIN VAI PARA SUPERADMIN
     * SE FOR SUPER ADMIN VAI DAR EXECAO FALANDO QUE JA E O ROLE MAXIMO
     */
    @PutMapping("/users/{userId}/upgrade")
    public ResponseEntity<?> upgradeUserRole(
            @PathVariable UUID userId,
            @CurrentUser User currentUser) {

        try {
            // Valida permissão de super admin
            superAdminService.validateSuperAdminPermission(currentUser);

            // Executa o upgrade
            User upgradedUser = superAdminService.upgradeUserRole(userId);
            UserDTO.UserResponse response = userService.convertToUserResponse(upgradedUser);

            return ResponseEntity.ok()
                    .body(new ApiResponse("Usuário promovido com sucesso", response));

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erro ao fazer upgrade do usuário {}", userId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    /**
     * FUNCOA DE FAZER DOWGRADE
     * SE FOR SUPER ADMIN VAI PARA ADMIN
     * SE FOR ADMIN VAI PARA USER
     * SE FOR USER VAI DAR EXECAO FALANDO QUE JA E O ROLE MINIMO
     */
    @PutMapping("/users/{userId}/downgrade")
    public ResponseEntity<?> downgradeUserRole(
            @PathVariable UUID userId,
            @CurrentUser User currentUser) {

        try {
            // Valida permissão de super admin
            superAdminService.validateSuperAdminPermission(currentUser);

            // Não permite que super admin faça downgrade de si mesmo
            if (userId.equals(currentUser.getId())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Não é possível fazer downgrade de si mesmo", null));
            }

            // Executa o downgrade
            User downgradedUser = superAdminService.downgradeUserRole(userId);
            UserDTO.UserResponse response = userService.convertToUserResponse(downgradedUser);

            return ResponseEntity.ok()
                    .body(new ApiResponse("Usuário rebaixado com sucesso", response));

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erro ao fazer downgrade do usuário {}", userId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    /**
     * FUNCAO PARA EXCLUIR/DELETAR USUARIO
     * Remove completamente o usuário do sistema
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(
            @PathVariable UUID userId,
            @CurrentUser User currentUser) {

        try {
            // Valida permissão de super admin
            superAdminService.validateSuperAdminPermission(currentUser);

            // Não permite que super admin delete a si mesmo
            if (userId.equals(currentUser.getId())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Não é possível deletar a si mesmo", null));
            }

            // Executa a exclusão
            superAdminService.deleteUser(userId);

            return ResponseEntity.ok()
                    .body(new ApiResponse("Usuário deletado com sucesso", null));

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erro ao deletar usuário {}", userId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    /**
     * FUNCOA PARA ATUALIZAR O PLANO DO USUSARIO POR 30 DIAS
     * Ativa/renova o plano do usuário
     */
    @PutMapping("/users/{userId}/plan/update")
    public ResponseEntity<?> updateUserPlan(
            @PathVariable UUID userId,
            @CurrentUser User currentUser) {

        try {
            // Valida permissão de super admin
            superAdminService.validateSuperAdminPermission(currentUser);

            // Atualiza o plano
            User updatedUser = superAdminService.updateUserPlan(userId);
            UserDTO.UserResponse response = userService.convertToUserResponse(updatedUser);

            return ResponseEntity.ok()
                    .body(new ApiResponse("Plano do usuário atualizado por 30 dias", response));

        } catch (Exception e) {
            log.error("Erro ao atualizar plano do usuário {}", userId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    /**
     * FUNCAO PARA CORTAR O PLANO DO USER
     * Suspende/desativa o plano do usuário
     */
    @PutMapping("/users/{userId}/plan/cut")
    public ResponseEntity<?> cutUserPlan(
            @PathVariable UUID userId,
            @CurrentUser User currentUser) {

        try {
            // Valida permissão de super admin
            superAdminService.validateSuperAdminPermission(currentUser);

            // Não permite cortar próprio plano
            if (userId.equals(currentUser.getId())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Não é possível cortar o próprio plano", null));
            }

            // Corta o plano
            User updatedUser = superAdminService.cutUserPlan(userId);
            UserDTO.UserResponse response = userService.convertToUserResponse(updatedUser);

            return ResponseEntity.ok()
                    .body(new ApiResponse("Plano do usuário foi cortado/suspenso", response));

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erro ao cortar plano do usuário {}", userId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }



    /**
     * FUNCOA PARA ATUALIZAR O PLANO DO USUSARIO POR 30 DIAS
     * Ativa/renova o plano do usuário
     */
    @PutMapping("/users/{userId}/not-blocked")
    public ResponseEntity<?> notBlocked(
            @PathVariable UUID userId,
            @CurrentUser User currentUser) {

        try {
            // Valida permissão de super admin
            superAdminService.validateSuperAdminPermission(currentUser);

            // Atualiza o plano
            User updatedUser = superAdminService.notBlock(userId);
            UserDTO.UserResponse response = userService.convertToUserResponse(updatedUser);

            return ResponseEntity.ok()
                    .body(new ApiResponse("Plano do usuário atualizado por 30 dias", response));

        } catch (Exception e) {
            log.error("Erro ao atualizar plano do usuário {}", userId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    @PutMapping("/users/{userId}/block")
    public ResponseEntity<?> block(
            @PathVariable UUID userId,
            @CurrentUser User currentUser) {

        try {
            // Valida permissão de super admin
            superAdminService.validateSuperAdminPermission(currentUser);

            // Não permite cortar próprio plano
            if (userId.equals(currentUser.getId())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse("Não é possível cortar o próprio plano", null));
            }

            // Corta o plano
            User updatedUser = superAdminService.block(userId);
            UserDTO.UserResponse response = userService.convertToUserResponse(updatedUser);

            return ResponseEntity.ok()
                    .body(new ApiResponse("Plano do usuário foi cortado/suspenso", response));

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erro ao cortar plano do usuário {}", userId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }


    /**
     * Endpoint para obter informações de um usuário específico
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserInfo(
            @PathVariable UUID userId,
            @CurrentUser User currentUser) {

        try {
            // Valida permissão de super admin
            superAdminService.validateSuperAdminPermission(currentUser);

            User user = superAdminService.getUserInfo(userId);
            UserDTO.UserResponse response = userService.convertToUserResponse(user);

            return ResponseEntity.ok()
                    .body(new ApiResponse("Informações do usuário obtidas com sucesso", response));

        } catch (Exception e) {
            log.error("Erro ao obter informações do usuário {}", userId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    /**
     * Classe para padronizar respostas da API
     */
    public static class ApiResponse {
        private String message;
        private Object data;

        public ApiResponse(String message, Object data) {
            this.message = message;
            this.data = data;
        }

        // Getters
        public String getMessage() {
            return message;
        }

        public Object getData() {
            return data;
        }

        // Setters
        public void setMessage(String message) {
            this.message = message;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }
}