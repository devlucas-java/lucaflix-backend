package com.lucaflix.controller;

import com.lucaflix.dto.admin.CreateMovieDTO;
import com.lucaflix.dto.admin.UpdateMovieDTO;
import com.lucaflix.dto.admin.stats.DetailedStatsDTO;
import com.lucaflix.dto.admin.stats.MediaStatsDTO;
import com.lucaflix.dto.media.MovieCompleteDTO;
import com.lucaflix.service.AdminMovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/movies")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminMovieController {

    private final AdminMovieService adminMovieService;

    // ==================== GERENCIAMENTO DE FILMES ====================

    @PostMapping
    public ResponseEntity<MovieCompleteDTO> createMovie(@Valid @RequestBody CreateMovieDTO createDTO) {
        try {
            MovieCompleteDTO createdMovie = adminMovieService.createMovie(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieCompleteDTO> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMovieDTO updateDTO) {
        try {
            MovieCompleteDTO updatedMovie = adminMovieService.updateMovie(id, updateDTO);
            return ResponseEntity.ok(updatedMovie);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        try {
            adminMovieService.deleteMovie(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieCompleteDTO> getMovieById(@PathVariable Long id) {
        try {
            MovieCompleteDTO movie = adminMovieService.getMovieById(id);
            return ResponseEntity.ok(movie);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<MovieCompleteDTO>> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataCadastro") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Page<MovieCompleteDTO> movies = adminMovieService.getAllMovies(page, size, sortBy, sortDir);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MovieCompleteDTO>> searchMovies(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MovieCompleteDTO> movies = adminMovieService.searchMovies(q, page, size);
        return ResponseEntity.ok(movies);
    }

    // ==================== ESTATÍSTICAS ====================

    @GetMapping("/stats")
    public ResponseEntity<MediaStatsDTO> getCompleteStats() {
        MediaStatsDTO stats = adminMovieService.getCompleteStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/detailed")
    public ResponseEntity<DetailedStatsDTO> getDetailedStats() {
        DetailedStatsDTO stats = adminMovieService.getDetailedStats();
        return ResponseEntity.ok(stats);
    }
}