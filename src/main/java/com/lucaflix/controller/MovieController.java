package com.lucaflix.controller;

import com.lucaflix.dto.request.movie.CreateMovieDTO;
import com.lucaflix.dto.request.movie.UpdateMovieDTO;
import com.lucaflix.dto.request.others.FilterDTO;
import com.lucaflix.dto.response.movie.MovieCompleteDTO;
import com.lucaflix.dto.response.movie.MovieSimpleDTO;
import com.lucaflix.dto.response.others.PaginatedResponseDTO;
import com.lucaflix.model.User;
import com.lucaflix.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> filterMovies(
            @RequestBody FilterDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.filterMovies(filter, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieCompleteDTO> getMovieById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {

        MovieCompleteDTO response = movieService.getMediaById(id, user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}/similar")
    public ResponseEntity<PaginatedResponseDTO<MovieSimpleDTO>> getSimilarMovies(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        PaginatedResponseDTO<MovieSimpleDTO> response = movieService.getSimilarMedia(id, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<MovieCompleteDTO> createMovie(
            @Valid @RequestBody CreateMovieDTO createDTO) {

        MovieCompleteDTO response = movieService.createMovie(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MovieCompleteDTO> updateMovie(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMovieDTO updateDTO) {

        MovieCompleteDTO response = movieService.updateMovie(updateDTO, id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable UUID id) {

        movieService.deleteMovie(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}