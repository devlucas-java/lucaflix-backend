package com.lucaflix.controller;

import com.lucaflix.dto.request.others.FilterDTO;
import com.lucaflix.dto.request.serie.CreateSerieDTO;
import com.lucaflix.dto.request.serie.UpdateSerieDTO;
import com.lucaflix.dto.response.others.PaginatedResponseDTO;
import com.lucaflix.dto.response.serie.SerieCompleteDTO;
import com.lucaflix.dto.response.serie.SerieSimpleDTO;
import com.lucaflix.model.User;
import com.lucaflix.security.OptionalAuthentication;
import com.lucaflix.security.SkipJwtAuthentication;
import com.lucaflix.service.SeriesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/series")
@RequiredArgsConstructor
public class SeriesController {

    private final SeriesService seriesService;

    @PostMapping
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<SerieSimpleDTO>> filterSeries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestBody FilterDTO filter) {

        PaginatedResponseDTO<SerieSimpleDTO> response = seriesService.filterSeries(filter, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @OptionalAuthentication
    public ResponseEntity<SerieCompleteDTO> getSeriesById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {

        SerieCompleteDTO response = seriesService.getSeriesById(id, user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}/similar")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<SerieSimpleDTO>> getSimilarSeries(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<SerieSimpleDTO> response = seriesService.getSimilarSeries(id, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<SerieCompleteDTO> createSeries(@Valid @RequestBody CreateSerieDTO createDTO) {

            SerieCompleteDTO createdSerie = seriesService.createSeries(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSerie);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SerieCompleteDTO> updateSeries(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSerieDTO updateDTO) {

            SerieCompleteDTO response = seriesService.updateSeries(updateDTO, id);
            return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeries(@PathVariable UUID id) {

            seriesService.deleteSeries(id);
            return ResponseEntity.status(HttpStatus.OK).build();
    }


}