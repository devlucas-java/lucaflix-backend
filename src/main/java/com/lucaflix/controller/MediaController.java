package com.lucaflix.controller;

import com.lucaflix.dto.media.MediaCompleteDTO;
import com.lucaflix.dto.media.MediaSimpleDTO;
import com.lucaflix.dto.media.PaginatedResponseDTO;
import com.lucaflix.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MediaController {

    private final MediaService mediaService;

    /**
     * Lista todas as mídias com paginação
     */
    @GetMapping
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getAllMedia(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getAllMedia(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista apenas séries com paginação
     */
    @GetMapping("/series")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getSeries(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista apenas filmes com paginação
     */
    @GetMapping("/filmes")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getFilmes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getFilmes(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Retorna as top 10 mídias com mais likes
     */
    @GetMapping("/top10")
    public ResponseEntity<List<MediaSimpleDTO>> getTop10MostLiked() {
        List<MediaSimpleDTO> response = mediaService.getTop10MostLiked();
        return ResponseEntity.ok(response);
    }

    /**
     * Busca mídias por título
     */
    @GetMapping("/search")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> searchByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.searchByTitle(title, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Retorna uma mídia completa por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MediaCompleteDTO> getMediaById(@PathVariable Long id) {
        UUID userId = getCurrentUserId();
        MediaCompleteDTO response = mediaService.getMediaById(id, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Adiciona uma mídia à lista do usuário
     */
    @PostMapping("/my-list/{id}")
    public ResponseEntity<String> addToMyList(@PathVariable("id") Long id) {
        try {
            UUID userId = getCurrentUserId();
            mediaService.addToMyList(userId, id);
            return ResponseEntity.ok("Mídia adicionada à sua lista com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Remove uma mídia da lista do usuário
     */
    @DeleteMapping("/my-list/{mediaId}")
    public ResponseEntity<String> removeFromMyList(@PathVariable Long mediaId) {
        try {
            UUID userId = getCurrentUserId();
            mediaService.removeFromMyList(userId, mediaId);
            return ResponseEntity.ok("Mídia removida da sua lista com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retorna a lista pessoal do usuário
     */
    @GetMapping("/my-list")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getMyList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        UUID userId = getCurrentUserId();
        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getMyList(userId, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Dá like em uma mídia
     */
    @PostMapping("/like/{id}")
    public ResponseEntity<String> likeMedia(@PathVariable("id") Long mediaId) {
        try {
            UUID userId = getCurrentUserId();
            mediaService.likeMedia(userId, mediaId);
            return ResponseEntity.ok("Like adicionado com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Remove like de uma mídia
     */
    @DeleteMapping("/like/{mediaId}")
    public ResponseEntity<String> unlikeMedia(@PathVariable Long mediaId) {
        try {
            UUID userId = getCurrentUserId();
            mediaService.unlikeMedia(userId, mediaId);
            return ResponseEntity.ok("Like removido com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Toggle like - Se não curtiu, curte. Se já curtiu, descurte.
     */
    @PostMapping("/like/toggle/{mediaId}")
    public ResponseEntity<String> toggleLike(@PathVariable Long mediaId) {
        try {
            UUID userId = getCurrentUserId();

            // Verifica se já curtiu
            MediaCompleteDTO media = mediaService.getMediaById(mediaId, userId);

            if (media.isUserLiked()) {
                mediaService.unlikeMedia(userId, mediaId);
                return ResponseEntity.ok("Like removido com sucesso!");
            } else {
                mediaService.likeMedia(userId, mediaId);
                return ResponseEntity.ok("Like adicionado com sucesso!");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Toggle lista - Se não está na lista, adiciona. Se já está, remove.
     */
    @PostMapping("/my-list/toggle/{mediaId}")
    public ResponseEntity<String> toggleMyList(@PathVariable Long mediaId) {
        try {
            UUID userId = getCurrentUserId();

            // Verifica se já está na lista
            MediaCompleteDTO media = mediaService.getMediaById(mediaId, userId);

            if (media.isInUserList()) {
                mediaService.removeFromMyList(userId, mediaId);
                return ResponseEntity.ok("Mídia removida da sua lista com sucesso!");
            } else {
                mediaService.addToMyList(userId, mediaId);
                return ResponseEntity.ok("Mídia adicionada à sua lista com sucesso!");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Busca filmes da lista pessoal do usuário
     */
    @GetMapping("/my-list/filmes")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getMyListFilmes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        UUID userId = getCurrentUserId();
        // Implementar no service se necessário
        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getMyList(userId, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca séries da lista pessoal do usuário
     */
    @GetMapping("/my-list/series")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getMyListSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        UUID userId = getCurrentUserId();
        // Implementar no service se necessário
        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getMyList(userId, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para admin - Estatísticas gerais
     */
    @GetMapping("/stats")
    public ResponseEntity<Object> getMediaStats() {
        // Implementar estatísticas se necessário
        return ResponseEntity.ok("Estatísticas não implementadas ainda");
    }

    /**
     * Método auxiliar para obter o ID do usuário atual
     */
    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuário não autenticado");
        }

        // Assumindo que o username é o UUID do usuário
        // Ajuste conforme sua implementação de autenticação
        try {
            return UUID.fromString(authentication.getName());
        } catch (Exception e) {
            // Se o authentication.getName() retorna o username em vez do UUID
            // você precisará buscar o usuário pelo username
            throw new RuntimeException("Erro ao obter ID do usuário: " + e.getMessage());
        }
    }

    /**
     * Endpoint público para mídia específica (sem informações do usuário)
     */
    @GetMapping("/public/{id}")
    public ResponseEntity<MediaCompleteDTO> getPublicMediaById(@PathVariable Long id) {
        MediaCompleteDTO response = mediaService.getMediaById(id, null);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint público para top 10
     */
    @GetMapping("/public/top10")
    public ResponseEntity<List<MediaSimpleDTO>> getPublicTop10MostLiked() {
        List<MediaSimpleDTO> response = mediaService.getTop10MostLiked();
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint público para busca
     */
    @GetMapping("/public/search")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> publicSearchByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.searchByTitle(title, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint público para listar todas as mídias
     */
    @GetMapping("/public")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getPublicAllMedia(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getAllMedia(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint público para séries
     */
    @GetMapping("/public/series")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getPublicSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getSeries(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint público para filmes
     */
    @GetMapping("/public/filmes")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getPublicFilmes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getFilmes(page, size);
        return ResponseEntity.ok(response);
    }
}