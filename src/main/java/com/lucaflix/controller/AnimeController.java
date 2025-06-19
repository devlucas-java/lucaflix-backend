package com.lucaflix.controller;

import com.lucaflix.dto.media.AnimeCompleteDTO;
import com.lucaflix.dto.media.AnimeFilter;
import com.lucaflix.dto.media.AnimeSimpleDTO;
import com.lucaflix.dto.media.PaginatedResponseDTO;
import com.lucaflix.model.User;
import com.lucaflix.model.enums.Categoria;
import com.lucaflix.security.CurrentUser;
import com.lucaflix.security.OptionalAuthentication;
import com.lucaflix.security.SkipJwtAuthentication;
import com.lucaflix.service.AnimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/animes")
@RequiredArgsConstructor
public class AnimeController {

    private final AnimeService animeService;

    /**
     * Filtrar animes com critérios específicos
     */
    @PostMapping("/filter")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<AnimeSimpleDTO>> filterAnimes(
            @RequestBody AnimeFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<AnimeSimpleDTO> response = animeService.filtrarAnime(filter, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Obter todos os animes com paginação
     */
    @GetMapping
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<AnimeSimpleDTO>> getAllAnimes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<AnimeSimpleDTO> response = animeService.getNewReleases(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Top 10 animes mais curtidos
     */
    @GetMapping("/top10")
    @SkipJwtAuthentication
    public ResponseEntity<List<AnimeSimpleDTO>> getTop10MostLiked() {
        List<AnimeSimpleDTO> animes = animeService.getTop10MostLiked();
        return ResponseEntity.ok(animes);
    }

    /**
     * Obter anime por ID
     */
    @GetMapping("/{id}")
    @OptionalAuthentication
    public ResponseEntity<AnimeCompleteDTO> getAnimeById(
            @PathVariable Long id,
            @CurrentUser User currentUser) {

        AnimeCompleteDTO anime = animeService.getAnimeById(id,
                currentUser != null ? currentUser.getId() : null);
        return ResponseEntity.ok(anime);
    }

    /**
     * Toggle like em um anime (requer autenticação)
     */
    @PostMapping("/{id}/like")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Boolean> toggleLike(
            @PathVariable Long id,
            @CurrentUser User currentUser) {

        boolean liked = animeService.toggleLike(currentUser.getId(), id);
        return ResponseEntity.ok(liked);
    }

    /**
     * Toggle anime na lista do usuário (requer autenticação)
     */
    @PostMapping("/{id}/my-list")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Boolean> toggleMyList(
            @PathVariable Long id,
            @CurrentUser User currentUser) {

        boolean added = animeService.toggleMyList(currentUser.getId(), id);
        return ResponseEntity.ok(added);
    }

    /**
     * Obter lista pessoal do usuário (requer autenticação)
     */
    @GetMapping("/my-list")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PaginatedResponseDTO<AnimeSimpleDTO>> getMyList(
            @CurrentUser User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<AnimeSimpleDTO> response = animeService.getMyList(currentUser.getId(), page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Animes populares (mais curtidos)
     */
    @GetMapping("/popular")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<AnimeSimpleDTO>> getPopularAnimes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<AnimeSimpleDTO> response = animeService.getPopularAnimes(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Novos lançamentos
     */
    @GetMapping("/new-releases")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<AnimeSimpleDTO>> getNewReleases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<AnimeSimpleDTO> response = animeService.getNewReleases(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Animes por categoria
     */
    @GetMapping("/category/{categoria}")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<AnimeSimpleDTO>> getAnimesByCategory(
            @PathVariable Categoria categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<AnimeSimpleDTO> response = animeService.getAnimeByCategory(categoria, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Animes com avaliação alta (≥ 7.0)
     */
    @GetMapping("/high-rated")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<AnimeSimpleDTO>> getHighRatedAnimes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<AnimeSimpleDTO> response = animeService.getHighRatedAnimes(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Recomendações personalizadas (requer autenticação)
     */
    @GetMapping("/recommendations")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PaginatedResponseDTO<AnimeSimpleDTO>> getRecommendations(
            @CurrentUser User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<AnimeSimpleDTO> response = animeService.getRecommendations(currentUser.getId(), page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Animes similares
     */
    @GetMapping("/{id}/similar")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<AnimeSimpleDTO>> getSimilarAnimes(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<AnimeSimpleDTO> response = animeService.getSimilarAnimes(id, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Animes por ano
     */
    @GetMapping("/year/{year}")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<AnimeSimpleDTO>> getAnimesByYear(
            @PathVariable Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<AnimeSimpleDTO> response = animeService.getAnimesByYear(year, page, size);
        return ResponseEntity.ok(response);
    }
}