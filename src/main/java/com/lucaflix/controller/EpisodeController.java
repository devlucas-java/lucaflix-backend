package com.lucaflix.controller;

import com.lucaflix.dto.request.serie.CreateEpisodeDTO;
import com.lucaflix.dto.request.serie.UpdateEpisodeDTO;
import com.lucaflix.dto.response.serie.EpisodeDTO;
import com.lucaflix.service.EpisodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
@RequestMapping("/api/admin/episodes")
public class EpisodeController {

    private final EpisodeService episodeService;


    @PostMapping("/{id}")
    public ResponseEntity<EpisodeDTO> createEpisode(
            @PathVariable Long id,
            @Valid @RequestBody CreateEpisodeDTO createDTO) {

        EpisodeDTO response = episodeService.createEpisode(createDTO, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EpisodeDTO> getEpisode(@PathVariable Long id) {
        EpisodeDTO response = episodeService.getEpisode(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EpisodeDTO> updateEpisode(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEpisodeDTO updateDTO) {

        EpisodeDTO response = episodeService.updateEpisode(updateDTO, id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEpisode(@PathVariable Long id) {
        episodeService.deleteEpisode(id);
        return ResponseEntity.noContent().build();
    }
}
