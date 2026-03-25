package com.lucaflix.controller;

import com.lucaflix.dto.response.others.PaginatedResponseDTO;
import com.lucaflix.dto.response.serie.SerieCompleteDTO;
import com.lucaflix.dto.response.serie.SerieSimpleDTO;
import com.lucaflix.model.User;
import com.lucaflix.model.enums.Categories;
import com.lucaflix.security.OptionalAuthentication;
import com.lucaflix.security.SkipJwtAuthentication;
import com.lucaflix.service.SerieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/series")
@RequiredArgsConstructor
public class SerieController {

    private final SerieService serieService;

    /**
     * Obter todas as séries com paginação
     */
    @GetMapping
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<SerieSimpleDTO>> getAllSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<SerieSimpleDTO> response = serieService.getAllSeries(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Top 10 séries mais curtidas
     */
    @GetMapping("/top10")
    @SkipJwtAuthentication
    public ResponseEntity<List<SerieSimpleDTO>> getTop10MostLiked() {
        List<SerieSimpleDTO> series = serieService.getTop10MostLiked();
        return ResponseEntity.ok(series);
    }

    /**
     * Obter série por ID
     */
    @GetMapping("/{id}")
    @OptionalAuthentication
    public ResponseEntity<SerieCompleteDTO> getSerieById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        SerieCompleteDTO serie = serieService.getSerieById(id,
                currentUser != null ? currentUser.getId() : null);
        return ResponseEntity.ok(serie);
    }

    /**
     * Toggle like em uma série (requer autenticação)
     */
    @PostMapping("/{id}/like")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Boolean> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        boolean liked = serieService.toggleLike(currentUser.getId(), id);
        return ResponseEntity.ok(liked);
    }

    /**
     * Toggle série na lista do usuário (requer autenticação)
     */
    @PostMapping("/{id}/my-list")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Boolean> toggleMyList(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        boolean added = serieService.toggleMyList(currentUser.getId(), id);
        return ResponseEntity.ok(added);
    }


    /**
     * Séries por categoria
     */
    @GetMapping("/category/{categoria}")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<SerieSimpleDTO>> getSeriesByCategory(
            @PathVariable Categories categories,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<SerieSimpleDTO> response = serieService.getSeriesByCategory(categories, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Séries populares (mais curtidas)
     */
    @GetMapping("/popular")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<SerieSimpleDTO>> getPopularSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        PaginatedResponseDTO<SerieSimpleDTO> response = serieService.getPopularSeries(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Séries com avaliação alta (≥ 7.0)
     */
    @GetMapping("/high-rated")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<SerieSimpleDTO>> getHighRatedSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        PaginatedResponseDTO<SerieSimpleDTO> response = serieService.getHighRatedSeries(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Séries recentes (ordenadas por ano de lançamento)
     */
    @GetMapping("/recent")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<SerieSimpleDTO>> getRecentSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        PaginatedResponseDTO<SerieSimpleDTO> response = serieService.getRecentSeries(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Séries por ano de lançamento
     */
    @GetMapping("/year/{year}")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<SerieSimpleDTO>> getSeriesByYear(
            @PathVariable Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        PaginatedResponseDTO<SerieSimpleDTO> response = serieService.getSeriesByYear(year, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Séries similares
     */
    @GetMapping("/{id}/similar")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<SerieSimpleDTO>> getSimilarSeries(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<SerieSimpleDTO> response = serieService.getSimilarSeries(id, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Recomendações personalizadas (requer autenticação)
     */
    @GetMapping("/recommendations")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PaginatedResponseDTO<SerieSimpleDTO>> getRecommendations(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        PaginatedResponseDTO<SerieSimpleDTO> response = serieService.getRecommendations(currentUser.getId(), page, size);
        return ResponseEntity.ok(response);
    }
}