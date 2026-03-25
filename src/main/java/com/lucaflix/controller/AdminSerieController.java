package com.lucaflix.controller;

import com.lucaflix.dto.admin.*;
import com.lucaflix.dto.response.serie.SerieCompleteDTO;
import com.lucaflix.model.Season;
import com.lucaflix.model.Episode;
import com.lucaflix.service.AdminSerieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/series")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@CrossOrigin(origins = "*")
public class AdminSerieController {

    private final AdminSerieService adminSerieService;

    // ==================== GERENCIAMENTO DE SÉRIES ====================

    @PostMapping
    public ResponseEntity<SerieCompleteDTO> createSerie(@Valid @RequestBody CreateSerieDTO createDTO) {
        try {
            SerieCompleteDTO createdSerie = adminSerieService.createSerie(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSerie);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SerieCompleteDTO> updateSerie(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSerieDTO updateDTO) {
        try {
            SerieCompleteDTO updatedSerie = adminSerieService.updateSerie(id, updateDTO);
            return ResponseEntity.ok(updatedSerie);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSerie(@PathVariable Long id) {
        try {
            adminSerieService.deleteSerie(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // ==================== GERENCIAMENTO DE TEMPORADAS ====================

    @PostMapping("/{serieId}/temporadas")
    public ResponseEntity<Season> createTemporada(
            @PathVariable Long serieId,
            @Valid @RequestBody CreateTemporadaDTO createDTO) {
        try {
            Season createdSeason = adminSerieService.createTemporada(serieId, createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSeason);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/temporadas/{temporadaId}")
    public ResponseEntity<Season> updateTemporada(
            @PathVariable Long temporadaId,
            @Valid @RequestBody UpdateTemporadaDTO updateDTO) {
        try {
            Season updatedSeason = adminSerieService.updateTemporada(temporadaId, updateDTO);
            return ResponseEntity.ok(updatedSeason);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/temporadas/{temporadaId}")
    public ResponseEntity<Void> deleteTemporada(@PathVariable Long temporadaId) {
        try {
            adminSerieService.deleteTemporada(temporadaId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{serieId}/temporadas")
    public ResponseEntity<List<Season>> getTemporadasBySerie(@PathVariable Long serieId) {
        try {
            List<Season> seasons = adminSerieService.getTemporadasBySerie(serieId);
            return ResponseEntity.ok(seasons);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== GERENCIAMENTO DE EPISÓDIOS ====================

    @PostMapping("/temporadas/{temporadaId}/episodios")
    public ResponseEntity<Episode> createEpisodio(
            @PathVariable Long temporadaId,
            @Valid @RequestBody CreateEpisodioDTO createDTO) {
        try {
            Episode createdEpisode = adminSerieService.createEpisodio(temporadaId, createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEpisode);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/episodios/{episodioId}")
    public ResponseEntity<Episode> updateEpisodio(
            @PathVariable Long episodioId,
            @Valid @RequestBody UpdateEpisodioDTO updateDTO) {
        try {
            Episode updatedEpisode = adminSerieService.updateEpisodio(episodioId, updateDTO);
            return ResponseEntity.ok(updatedEpisode);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/episodios/{episodioId}")
    public ResponseEntity<Void> deleteEpisodio(@PathVariable Long episodioId) {
        try {
            adminSerieService.deleteEpisodio(episodioId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/temporadas/{temporadaId}/episodios")
    public ResponseEntity<List<Episode>> getEpisodiosByTemporada(@PathVariable Long temporadaId) {
        try {
            List<Episode> episodes = adminSerieService.getEpisodiosByTemporada(temporadaId);
            return ResponseEntity.ok(episodes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{serieId}/episodios")
    public ResponseEntity<List<Episode>> getEpisodiosBySerie(@PathVariable Long serieId) {
        try {
            List<Episode> episodes = adminSerieService.getEpisodiosBySerie(serieId);
            return ResponseEntity.ok(episodes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para criar uma série completa com todas as temporadas e episódios
     */
    @PostMapping("/complete")
    public ResponseEntity<?> createSerieComplete(@Valid @RequestBody CreateSerieCompleteDTO createDTO) {
        try {
            SerieCompleteDTO createdSerie = adminSerieService.createSerieComplete(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSerie);
        } catch (RuntimeException e) {
            // Log do erro para debug
            System.err.println("Erro ao criar série completa: " + e.getMessage());
            e.printStackTrace();

            // Retornar erro detalhado
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Erro ao criar série completa",
                    "message", e.getMessage(),
                    "timestamp", new Date()
            ));
        } catch (Exception e) {
            // Log do erro para debug
            System.err.println("Erro inesperado ao criar série completa: " + e.getMessage());
            e.printStackTrace();

            // Retornar erro genérico
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Erro interno do servidor",
                    "message", "Erro inesperado ao processar a solicitação",
                    "timestamp", new Date()
            ));
        }
    }
}