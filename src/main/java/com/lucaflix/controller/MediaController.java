//package com.lucaflix.controller;
//
//import com.lucaflix.dto.media.MovieCompleteDTO;
//import com.lucaflix.dto.media.MovieFilter;
//import com.lucaflix.dto.media.MovieSimpleDTO;
//import com.lucaflix.dto.media.PaginatedResponseDTO;
//import com.lucaflix.model.User;
//import com.lucaflix.model.enums.Categoria;
//import com.lucaflix.security.CurrentUser;
//import com.lucaflix.service.MovieService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/media")
//@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
//public class MediaController {
//
//    private final MovieService movieService;
//
//    /**
//     * Lista todas as mídias com paginação
//     */
//    @GetMapping("/search")
//    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> buscarMediaPorFiltro(
//            @RequestParam(required = false) Boolean isFilme,
//            @RequestParam(required = false) String title,
//            @RequestParam(required = false) Double avaliacao,
//            @RequestParam(required = false) String categoria,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//
//        MovieFilter filter = new MovieFilter();
//        filter.setIsFilme(isFilme);
//        filter.setTitle(title);
//        filter.setAvaliacao(avaliacao);
//
//        if (categoria != null) {
//            filter.setCategoria(Categoria.valueOf(categoria.toUpperCase()));
//        }
//
//        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.filtrarMedia(filter, page, size);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Retorna as top 10 mídias com mais likes
//     */
//    @GetMapping("/top10")
//    public ResponseEntity<List<MovieSimpleDTO>> getTop10MostLiked() {
//        List<MovieSimpleDTO> response = movieService.getTop10MostLiked();
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Retorna uma mídia completa por ID
//     */
//    @GetMapping("/{id}")
//    public ResponseEntity<MovieCompleteDTO> getMediaById(@PathVariable Long id, @CurrentUser User user) {
//
//        MovieCompleteDTO response = movieService.getMediaById(id, user.getId());
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Retorna a lista pessoal do usuário
//     */
//    @GetMapping("/my-list")
//    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getMyList(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            @CurrentUser User user) {
//
//
//        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getMyList(user.getId(), page, size);
//        return ResponseEntity.ok(response);
//    }
//
//
//    /**
//     * Toggle like - Se não curtiu, curte. Se já curtiu, descurte.
//     */
//    @PostMapping("/like/toggle/{mediaId}")
//    public ResponseEntity<String> toggleLike(@PathVariable Long mediaId, @CurrentUser User user) {
//        try {
//            // Verifica se já curtiu
//            MovieCompleteDTO media = movieService.getMediaById(mediaId, user.getId());
//
//            if (media.isUserLiked()) {
//                movieService.unlikeMedia(user.getId(), mediaId);
//                return ResponseEntity.ok("Like removido com sucesso!");
//            } else {
//                movieService.likeMedia(user.getId(), mediaId);
//                return ResponseEntity.ok("Like adicionado com sucesso!");
//            }
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    /**
//     * Toggle lista - Se não está na lista, adiciona. Se já está, remove.
//     */
//    @PostMapping("/my-list/toggle/{mediaId}")
//    public ResponseEntity<String> toggleMyList(@PathVariable Long mediaId, @CurrentUser User user) {
//        try {
//            // Verifica se já está na lista
//            MovieCompleteDTO media = movieService.getMediaById(mediaId, user.getId());
//
//            if (media.isInUserList()) {
//                movieService.removeFromMyList(user.getId(), mediaId);
//                return ResponseEntity.ok("Mídia removida da sua lista com sucesso!");
//            } else {
//                movieService.addToMyList(user.getId(), mediaId);
//                return ResponseEntity.ok("Mídia adicionada à sua lista com sucesso!");
//            }
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//
//    /**
//     * Endpoint público para mídia específica (sem informações do usuário)
//     */
//    @GetMapping("/public/{id}")
//    public ResponseEntity<MovieCompleteDTO> getPublicMediaById(@PathVariable Long id) {
//        MovieCompleteDTO response = movieService.getMediaById(id, null);
//        return ResponseEntity.ok(response);
//    }
//
//
//
//
//
//
//
//    // Adicione estes novos endpoints na classe MediaController
//
//    /**
//     * Top 20 mídias com avaliação acima de 7.0
//     */
//    @GetMapping("/trending/high-rated")
//    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getHighRatedMedia(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//
//        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getHighRatedMedia(page, size);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Novos lançamentos - últimas mídias adicionadas
//     */
//    @GetMapping("/trending/new-releases")
//    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getNewReleases(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//
//        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getNewReleases(page, size);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Apenas filmes
//     */
//    @GetMapping("/movies")
//    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getMovies(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//
//        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getMovies(page, size);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Apenas séries
//     */
//    @GetMapping("/series")
//    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getSeries(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//
//        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getSeries(page, size);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Mídias por categoria específica
//     */
//    @GetMapping("/category/{categoria}")
//    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getMediaByCategory(
//            @PathVariable String categoria,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//
//        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getMediaByCategory(categoria, page, size);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Filmes populares (mais curtidos)
//     */
//    @GetMapping("/trending/popular-movies")
//    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getPopularMovies(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//
//        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getPopularMovies(page, size);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Séries populares (mais curtidas)
//     */
//    @GetMapping("/trending/popular-series")
//    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getPopularSeries(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//
//        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getPopularSeries(page, size);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Filmes recentes
//     */
//    @GetMapping("/movies/recent")
//    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getRecentMovies(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//
//        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getRecentMovies(page, size);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Séries recentes
//     */
//    @GetMapping("/series/recent")
//    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getRecentSeries(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//
//        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getRecentSeries(page, size);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Mídias por faixa etária
//     */
//    @GetMapping("/age-rating/{minAge}")
//    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getMediaByAgeRating(
//            @PathVariable String minAge,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//
//        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getMediaByAgeRating(minAge, page, size);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Mídias por duração (filmes curtos/longos)
//     */
//    @GetMapping("/duration")
//    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getMediaByDuration(
//            @RequestParam(required = false) Integer minDuration,
//            @RequestParam(required = false) Integer maxDuration,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//
//        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getMediaByDuration(minDuration, maxDuration, page, size);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Mídias por ano específico
//     */
//    @GetMapping("/year/{year}")
//    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getMediaByYear(
//            @PathVariable Integer year,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//
//        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getMediaByYear(year, page, size);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Recomendações baseadas no que o usuário curtiu
//     */
//    @GetMapping("/recommendations")
//    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getRecommendations(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            @CurrentUser User user) {
//
//        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getRecommendations(user.getId(), page, size);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Continuar assistindo (baseado na lista do usuário)
//     */
//    @GetMapping("/continue-watching")
//    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getContinueWatching(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @CurrentUser User user) {
//
//        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getContinueWatching(user.getId(), page, size);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Mídias similares a uma específica (mesmo gênero/categoria)
//     */
//    @GetMapping("/{id}/similar")
//    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getSimilarMedia(
//            @PathVariable Long id,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//
//        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getSimilarMedia(id, page, size);
//        return ResponseEntity.ok(response);
//    }
//}