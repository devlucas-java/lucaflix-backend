package com.lucaflix.controller;

import com.lucaflix.dto.request.anime.CreateAnimeDTO;
import com.lucaflix.dto.request.anime.UpdateAnimeDTO;
import com.lucaflix.dto.request.others.FilterDTO;
import com.lucaflix.dto.response.anime.AnimeCompleteDTO;
import com.lucaflix.dto.response.anime.AnimeSimpleDTO;
import com.lucaflix.dto.response.others.PaginatedResponseDTO;
import com.lucaflix.model.User;
import com.lucaflix.service.AnimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/animes")
@RequiredArgsConstructor
public class AnimeController {

    private final AnimeService animeService;

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponseDTO<AnimeSimpleDTO>> filterAnime(
            @RequestBody FilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PaginatedResponseDTO<AnimeSimpleDTO> response = animeService.filterAnime(filter, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimeCompleteDTO> getAnimeById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {
        AnimeCompleteDTO anime = animeService.getAnimeById(id, user);
        return ResponseEntity.status(HttpStatus.OK).body(anime);
    }

    @GetMapping("/{id}/similar")
    public ResponseEntity<PaginatedResponseDTO<AnimeSimpleDTO>> getSimilarAnime(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PaginatedResponseDTO<AnimeSimpleDTO> response = animeService.getSimilarAnime(id, page, size);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AnimeCompleteDTO> createAnime(@Valid @RequestBody CreateAnimeDTO createDTO) {
            AnimeCompleteDTO createdAnime = animeService.createAnime(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAnime);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AnimeCompleteDTO> updateAnime(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAnimeDTO updateDTO) {
            AnimeCompleteDTO updatedAnime = animeService.updateAnime(updateDTO, id);
            return ResponseEntity.ok(updatedAnime);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnime(@PathVariable UUID id) {
            animeService.deleteAnime(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}