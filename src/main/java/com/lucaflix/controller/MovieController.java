package com.lucaflix.controller;

import com.lucaflix.dto.media.MovieCompleteDTO;
import com.lucaflix.dto.media.MovieFilter;
import com.lucaflix.dto.media.MovieSimpleDTO;
import com.lucaflix.dto.media.PaginatedResponseDTO;
import com.lucaflix.model.User;
import com.lucaflix.model.enums.Categoria;
import com.lucaflix.security.CurrentUser;
import com.lucaflix.security.OptionalAuthentication;
import com.lucaflix.security.SkipJwtAuthentication;
import com.lucaflix.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    /**
     * Filtrar filmes com critérios específicos
     */
    @PostMapping("/filter")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> filterMovies(
            @RequestBody MovieFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.filtrarMedia(filter, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Obter todos os filmes com paginação
     */
    @GetMapping
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getNewReleases(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Top 10 filmes mais curtidos
     */
    @GetMapping("/top10")
    @SkipJwtAuthentication
    public ResponseEntity<List<MovieSimpleDTO>> getTop10MostLiked() {
        List<MovieSimpleDTO> movies = movieService.getTop10MostLiked();
        return ResponseEntity.ok(movies);
    }

    /**
     * Obter filme por ID
     */
    @GetMapping("/{id}")
    @OptionalAuthentication
    public ResponseEntity<MovieCompleteDTO> getMovieById(
            @PathVariable Long id,
            @CurrentUser User currentUser) {

        MovieCompleteDTO movie = movieService.getMediaById(id,
                currentUser != null ? currentUser.getId() : null);
        return ResponseEntity.ok(movie);
    }

    /**
     * Toggle like em um filme (requer autenticação)
     */
    @PostMapping("/{id}/like")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Boolean> toggleLike(
            @PathVariable Long id,
            @CurrentUser User currentUser) {

        boolean liked = movieService.toggleLike(currentUser.getId(), id);
        return ResponseEntity.ok(liked);
    }

    /**
     * Toggle filme na lista do usuário (requer autenticação)
     */
    @PostMapping("/{id}/my-list")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Boolean> toggleMyList(
            @PathVariable Long id,
            @CurrentUser User currentUser) {

        boolean added = movieService.toggleMyList(currentUser.getId(), id);
        return ResponseEntity.ok(added);
    }

    /**
     * Obter lista pessoal do usuário (requer autenticação)
     */
    @GetMapping("/my-list")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getMyList(
            @CurrentUser User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getMyList(currentUser.getId(), page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Filmes populares (mais curtidos)
     */
    @GetMapping("/popular")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getPopularMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getPopularMovies(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Novos lançamentos
     */
    @GetMapping("/new-releases")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getNewReleases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getNewReleases(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Filmes por categoria
     */
    @GetMapping("/category/{categoria}")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getMoviesByCategory(
            @PathVariable Categoria categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getMediaByCategory(categoria, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Filmes com avaliação alta (≥ 7.0)
     */
    @GetMapping("/high-rated")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getHighRatedMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getHighRatedMedia(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Recomendações personalizadas (requer autenticação)
     */
    @GetMapping("/recommendations")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getRecommendations(
            @CurrentUser User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getRecommendations(currentUser.getId(), page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Filmes similares
     */
    @GetMapping("/{id}/similar")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getSimilarMovies(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getSimilarMedia(id, page, size);
        return ResponseEntity.ok(response);
    }
}