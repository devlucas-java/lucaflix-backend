package com.lucaflix.controller;

import com.lucaflix.dto.request.anime.CreateAnimeDTO;
import com.lucaflix.dto.request.anime.UpdateAnimeDTO;
import com.lucaflix.dto.response.anime.AnimeCompleteDTO;
import com.lucaflix.service.AdminAnimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/animes")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@CrossOrigin(origins = "*")
public class AdminAnimeController {

    private final AdminAnimeService adminAnimeService;

    // ==================== GERENCIAMENTO DE ANIMES ====================

    @PostMapping
    public ResponseEntity<AnimeCompleteDTO> createAnime(@Valid @RequestBody CreateAnimeDTO createDTO) {
        try {
            AnimeCompleteDTO createdAnime = adminAnimeService.createAnime(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAnime);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimeCompleteDTO> updateAnime(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAnimeDTO updateDTO) {
        try {
            AnimeCompleteDTO updatedAnime = adminAnimeService.updateAnime(id, updateDTO);
            return ResponseEntity.ok(updatedAnime);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnime(@PathVariable Long id) {
        try {
            adminAnimeService.deleteAnime(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimeCompleteDTO> getAnimeById(@PathVariable Long id) {
        try {
            AnimeCompleteDTO anime = adminAnimeService.getAnimeById(id);
            return ResponseEntity.ok(anime);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<AnimeCompleteDTO>> getAllAnimes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataCadastro") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Page<AnimeCompleteDTO> animes = adminAnimeService.getAllAnimes(page, size, sortBy, sortDir);
        return ResponseEntity.ok(animes);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AnimeCompleteDTO>> searchAnimes(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AnimeCompleteDTO> animes = adminAnimeService.searchAnimes(q, page, size);
        return ResponseEntity.ok(animes);
    }

}