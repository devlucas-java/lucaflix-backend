package com.lucaflix.controller;

import com.lucaflix.dto.response.page.PaginatedResponseDTO;
import com.lucaflix.dto.request.user.UpdateUserDTO;
import com.lucaflix.model.User;
import com.lucaflix.service.SuperAdminService;
import com.lucaflix.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<PaginatedResponseDTO<UpdateUserDTO.UserListResponse>> searchUsers(
            @RequestParam(required = false) String searchTerm,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(superAdminService.searchUsers(searchTerm, pageable));
    }

    @PutMapping("/users/{userId}/promote")
    public ResponseEntity<ApiResponse> promoteUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User currentUser) {

        try {
            User promotedUser = superAdminService.promoteUser(userId, currentUser);
            UpdateUserDTO.UserResponse response = userService.convertToUserResponse(promotedUser);

            return ResponseEntity.ok(new ApiResponse("Usuário promovido com sucesso", response));

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erro ao promover usuário {}", userId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    @PutMapping("/users/{userId}/demote")
    public ResponseEntity<ApiResponse> demoteUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User currentUser) {

        try {
            User demotedUser = superAdminService.demoteUser(userId, currentUser);
            UpdateUserDTO.UserResponse response = userService.convertToUserResponse(demotedUser);

            return ResponseEntity.ok(new ApiResponse("Usuário rebaixado com sucesso", response));

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erro ao rebaixar usuário {}", userId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User currentUser) {

        try {
            superAdminService.deleteUser(userId, currentUser);
            return ResponseEntity.ok(new ApiResponse("Usuário deletado com sucesso", null));

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erro ao deletar usuário {}", userId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    @PutMapping("/users/{userId}/plan/upgrade")
    public ResponseEntity<ApiResponse> upgradeUserPlan(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User currentUser) {

        try {
            User updatedUser = superAdminService.updateUserPlan(userId, currentUser);
            UpdateUserDTO.UserResponse response = userService.convertToUserResponse(updatedUser);

            return ResponseEntity.ok(new ApiResponse("Plano do usuário atualizado para PREMIUM", response));

        } catch (Exception e) {
            log.error("Erro ao atualizar plano do usuário {}", userId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    @PutMapping("/users/{userId}/plan/cut")
    public ResponseEntity<ApiResponse> cutUserPlan(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User currentUser) {

        try {
            User updatedUser = superAdminService.cutUserPlan(userId, currentUser);
            UpdateUserDTO.UserResponse response = userService.convertToUserResponse(updatedUser);

            return ResponseEntity.ok(new ApiResponse("Plano do usuário foi cortado para FREE", response));

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erro ao cortar plano do usuário {}", userId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    @PutMapping("/users/{userId}/block")
    public ResponseEntity<ApiResponse> blockUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User currentUser) {

        try {
            User blockedUser = superAdminService.blockUser(userId, currentUser);
            UpdateUserDTO.UserResponse response = userService.convertToUserResponse(blockedUser);

            return ResponseEntity.ok(new ApiResponse("Usuário bloqueado com sucesso", response));

        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erro ao bloquear usuário {}", userId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    @PutMapping("/users/{userId}/unblock")
    public ResponseEntity<ApiResponse> unblockUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User currentUser) {

        try {
            User unblockedUser = superAdminService.unblockUser(userId, currentUser);
            UpdateUserDTO.UserResponse response = userService.convertToUserResponse(unblockedUser);

            return ResponseEntity.ok(new ApiResponse("Usuário desbloqueado com sucesso", response));

        } catch (Exception e) {
            log.error("Erro ao desbloquear usuário {}", userId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse> getUserInfo(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User currentUser) {

        try {
            User user = superAdminService.getUserInfo(userId, currentUser);
            UpdateUserDTO.UserResponse response = userService.convertToUserResponse(user);

            return ResponseEntity.ok(new ApiResponse("Informações do usuário obtidas com sucesso", response));

        } catch (Exception e) {
            log.error("Erro ao obter informações do usuário {}", userId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    // ==================== ENDPOINTS PARA GERENCIAR LIKES DE CONTEÚDO ====================

    @DeleteMapping("/content/movies/{movieId}/likes")
    public ResponseEntity<ApiResponse> removeAllMovieLikes(
            @PathVariable Long movieId,
            @AuthenticationPrincipal User currentUser) {

        try {
            superAdminService.removeAllMovieLikes(movieId, currentUser);
            return ResponseEntity.ok(new ApiResponse("Todos os likes do filme foram removidos", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erro ao remover likes do filme {}", movieId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    @DeleteMapping("/content/series/{serieId}/likes")
    public ResponseEntity<ApiResponse> removeAllSerieLikes(
            @PathVariable Long serieId,
            @AuthenticationPrincipal User currentUser) {

        try {
            superAdminService.removeAllSerieLikes(serieId, currentUser);
            return ResponseEntity.ok(new ApiResponse("Todos os likes da série foram removidos", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erro ao remover likes da série {}", serieId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    @DeleteMapping("/content/animes/{animeId}/likes")
    public ResponseEntity<ApiResponse> removeAllAnimeLikes(
            @PathVariable Long animeId,
            @AuthenticationPrincipal User currentUser) {

        try {
            superAdminService.removeAllAnimeLikes(animeId, currentUser);
            return ResponseEntity.ok(new ApiResponse("Todos os likes do anime foram removidos", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erro ao remover likes do anime {}", animeId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    // ==================== ENDPOINTS PARA GERENCIAR LISTAS DE CONTEÚDO ====================

    @DeleteMapping("/content/movies/{movieId}/lists")
    public ResponseEntity<ApiResponse> removeMovieFromAllLists(
            @PathVariable Long movieId,
            @AuthenticationPrincipal User currentUser) {

        try {
            superAdminService.removeAllMovieFromLists(movieId, currentUser);
            return ResponseEntity.ok(new ApiResponse("Filme removido de todas as listas", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erro ao remover filme {} das listas", movieId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    @DeleteMapping("/content/series/{serieId}/lists")
    public ResponseEntity<ApiResponse> removeSerieFromAllLists(
            @PathVariable Long serieId,
            @AuthenticationPrincipal User currentUser) {

        try {
            superAdminService.removeAllSerieFromLists(serieId, currentUser);
            return ResponseEntity.ok(new ApiResponse("Série removida de todas as listas", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erro ao remover série {} das listas", serieId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    @DeleteMapping("/content/animes/{animeId}/lists")
    public ResponseEntity<ApiResponse> removeAnimeFromAllLists(
            @PathVariable Long animeId,
            @AuthenticationPrincipal User currentUser) {

        try {
            superAdminService.removeAllAnimeFromLists(animeId, currentUser);
            return ResponseEntity.ok(new ApiResponse("Anime removido de todas as listas", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erro ao remover anime {} das listas", animeId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    // ==================== ENDPOINTS PARA LIMPEZA COMPLETA DE CONTEÚDO ====================

    @DeleteMapping("/content/movies/{movieId}/interactions")
    public ResponseEntity<ApiResponse> cleanAllMovieInteractions(
            @PathVariable Long movieId,
            @AuthenticationPrincipal User currentUser) {

        try {
            superAdminService.cleanAllMovieInteractions(movieId, currentUser);
            return ResponseEntity.ok(new ApiResponse("Todas as interações do filme foram limpas", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erro ao limpar interações do filme {}", movieId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    @DeleteMapping("/content/series/{serieId}/interactions")
    public ResponseEntity<ApiResponse> cleanAllSerieInteractions(
            @PathVariable Long serieId,
            @AuthenticationPrincipal User currentUser) {

        try {
            superAdminService.cleanAllSerieInteractions(serieId, currentUser);
            return ResponseEntity.ok(new ApiResponse("Todas as interações da série foram limpas", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erro ao limpar interações da série {}", serieId, e);
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse("Erro interno do servidor", null));
        }
    }

    @DeleteMapping("/content/animes/{animeId}/interactions")
    public ResponseEntity<ApiResponse> cleanAllAnimeInteractions(
            @PathVariable Long animeId,
            @AuthenticationPrincipal User currentUser) {

        try {
            superAdminService.cleanAllAnimeInteractions(animeId, currentUser);
            return ResponseEntity.ok(new ApiResponse("Todas as interações do anime foram limpas", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Erro: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("Erro ao limpar interações do anime {}", animeId, e);
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