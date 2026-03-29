package com.lucaflix.controller;

import com.lucaflix.dto.request.serie.CreateSeasonDTO;
import com.lucaflix.dto.request.serie.UpdateSeasonDTO;
import com.lucaflix.dto.response.serie.SeasonDTO;
import com.lucaflix.service.SeasonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/seasons")
public class SeasonController {

    private final SeasonService seasonService;

    @GetMapping("/{id}")
    public ResponseEntity<SeasonDTO> getSeason(@PathVariable Long id) {
        SeasonDTO response = seasonService.getSeason(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<SeasonDTO> createSeason(@Valid @RequestBody CreateSeasonDTO createDTO, @PathVariable UUID id) {
        SeasonDTO response = seasonService.createSeason(createDTO, id);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SeasonDTO> updateSeason(@Valid @RequestBody UpdateSeasonDTO createDTO, @PathVariable Long id) {
        SeasonDTO response = seasonService.updateSeason(createDTO, id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeason(@PathVariable Long id) {
        seasonService.deleteSeason(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
