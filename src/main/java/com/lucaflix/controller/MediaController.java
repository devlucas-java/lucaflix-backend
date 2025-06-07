package com.lucaflix.controller;

import com.lucaflix.dto.media.MediaCompleteDTO;
import com.lucaflix.dto.media.MediaFilter;
import com.lucaflix.dto.media.MediaSimpleDTO;
import com.lucaflix.dto.media.PaginatedResponseDTO;
import com.lucaflix.model.User;
import com.lucaflix.model.enums.Categoria;
import com.lucaflix.security.CurrentUser;
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
    @GetMapping("/search")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> buscarMediaPorFiltro(
            @RequestParam(required = false) Boolean isFilme,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Double avaliacao,
            @RequestParam(required = false) String anoLancamentoInicio,
            @RequestParam(required = false) String anoLancamentoFim,
            @RequestParam(required = false) String categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        MediaFilter filter = new MediaFilter();
        filter.setIsFilme(isFilme);
        filter.setTitle(title);
        filter.setAvaliacao(avaliacao);

//        if (anoLancamentoInicio != null) {
//            filter.setAnoLancamentoInicio(LocalDate.parse(anoLancamentoInicio, DateTimeFormatter.ISO_DATE));
//        }
//
//        if (anoLancamentoFim != null) {
//            filter.setAnoLancamentoFim(LocalDate.parse(anoLancamentoFim, DateTimeFormatter.ISO_DATE));
//        }

        if (categoria != null) {
            filter.setCategoria(Categoria.valueOf(categoria.toUpperCase()));
        }

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.filtrarMedia(filter, page, size);
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
     * Retorna uma mídia completa por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MediaCompleteDTO> getMediaById(@PathVariable Long id, @CurrentUser User user) {

        MediaCompleteDTO response = mediaService.getMediaById(id, user.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Adiciona uma mídia à lista do usuário
     */
    @PostMapping("/my-list/{id}")
    public ResponseEntity<String> addToMyList(@PathVariable("id") Long id, @CurrentUser User user) {
        try {
            mediaService.addToMyList(user.getId(), id);
            return ResponseEntity.ok("Mídia adicionada à sua lista com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Remove uma mídia da lista do usuário
     */
    @DeleteMapping("/my-list/{mediaId}")
    public ResponseEntity<String> removeFromMyList(@PathVariable Long mediaId, @CurrentUser User user) {
        try {

            mediaService.removeFromMyList(user.getId(), mediaId);
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
            @RequestParam(defaultValue = "20") int size,
            @CurrentUser User user) {


        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getMyList(user.getId(), page, size);
        return ResponseEntity.ok(response);
    }


    /**
     * Toggle like - Se não curtiu, curte. Se já curtiu, descurte.
     */
    @PostMapping("/like/toggle/{mediaId}")
    public ResponseEntity<String> toggleLike(@PathVariable Long mediaId, @CurrentUser User user) {
        try {
            // Verifica se já curtiu
            MediaCompleteDTO media = mediaService.getMediaById(mediaId, user.getId());

            if (media.isUserLiked()) {
                mediaService.unlikeMedia(user.getId(), mediaId);
                return ResponseEntity.ok("Like removido com sucesso!");
            } else {
                mediaService.likeMedia(user.getId(), mediaId);
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
    public ResponseEntity<String> toggleMyList(@PathVariable Long mediaId, @CurrentUser User user) {
        try {


            // Verifica se já está na lista
            MediaCompleteDTO media = mediaService.getMediaById(mediaId, user.getId());

            if (media.isInUserList()) {
                mediaService.removeFromMyList(user.getId(), mediaId);
                return ResponseEntity.ok("Mídia removida da sua lista com sucesso!");
            } else {
                mediaService.addToMyList(user.getId(), mediaId);
                return ResponseEntity.ok("Mídia adicionada à sua lista com sucesso!");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    /**
     * Metodo auxiliar para obter o ID do usuário atual
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







    // Adicione estes novos endpoints na classe MediaController

    /**
     * Top 20 mídias com avaliação acima de 7.0
     */
    @GetMapping("/trending/high-rated")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getHighRatedMedia(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getHighRatedMedia(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Novos lançamentos - últimas mídias adicionadas
     */
    @GetMapping("/trending/new-releases")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getNewReleases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getNewReleases(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Apenas filmes
     */
    @GetMapping("/movies")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getMovies(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Apenas séries
     */
    @GetMapping("/series")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getSeries(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Mídias por categoria específica
     */
    @GetMapping("/category/{categoria}")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getMediaByCategory(
            @PathVariable String categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getMediaByCategory(categoria, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Filmes populares (mais curtidos)
     */
    @GetMapping("/trending/popular-movies")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getPopularMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getPopularMovies(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Séries populares (mais curtidas)
     */
    @GetMapping("/trending/popular-series")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getPopularSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getPopularSeries(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Filmes recentes
     */
    @GetMapping("/movies/recent")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getRecentMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getRecentMovies(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Séries recentes
     */
    @GetMapping("/series/recent")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getRecentSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getRecentSeries(page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Mídias por faixa etária
     */
    @GetMapping("/age-rating/{minAge}")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getMediaByAgeRating(
            @PathVariable String minAge,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getMediaByAgeRating(minAge, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Mídias por duração (filmes curtos/longos)
     */
    @GetMapping("/duration")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getMediaByDuration(
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getMediaByDuration(minDuration, maxDuration, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Mídias por ano específico
     */
    @GetMapping("/year/{year}")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getMediaByYear(
            @PathVariable Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getMediaByYear(year, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Recomendações baseadas no que o usuário curtiu
     */
    @GetMapping("/recommendations")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getRecommendations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUser User user) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getRecommendations(user.getId(), page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Continuar assistindo (baseado na lista do usuário)
     */
    @GetMapping("/continue-watching")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getContinueWatching(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @CurrentUser User user) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getContinueWatching(user.getId(), page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Mídias similares a uma específica (mesmo gênero/categoria)
     */
    @GetMapping("/{id}/similar")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> getSimilarMedia(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.getSimilarMedia(id, page, size);
        return ResponseEntity.ok(response);
    }
}