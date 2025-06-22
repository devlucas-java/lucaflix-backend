package com.lucaflix.controller;

import com.lucaflix.dto.admin.stats.*;
import com.lucaflix.service.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StatsController {

    private final StatusService statusService;

    @GetMapping("/general")
    public ResponseEntity<AllStatsDTO.GeneralStatsDTO> getGeneralStats() {
        return ResponseEntity.ok(statusService.getGeneralStats());
    }

    @GetMapping("/movies")
    public ResponseEntity<AllStatsDTO.MovieStatsDTO> getMovieStats() {
        return ResponseEntity.ok(statusService.getMovieStats());
    }

    @GetMapping("/series")
    public ResponseEntity<AllStatsDTO.SerieStatsDTO> getSerieStats() {
        return ResponseEntity.ok(statusService.getSerieStats());
    }

    @GetMapping("/animes")
    public ResponseEntity<AllStatsDTO.AnimeStatsDTO> getAnimeStats() {
        return ResponseEntity.ok(statusService.getAnimeStats());
    }

    @GetMapping("/tvs")
    public ResponseEntity<AllStatsDTO.TvStatsDTO> getTvStats() {
        return ResponseEntity.ok(statusService.getTvStats());
    }

    @GetMapping("/users")
    public ResponseEntity<AllStatsDTO.UserStatsDTO> getUserStats() {
        return ResponseEntity.ok(statusService.getUserStats());
    }

    @GetMapping("/all")
    public ResponseEntity<AllStatsDTO> getAllStats() {
        AllStatsDTO allStats = new AllStatsDTO();
        allStats.setGeneral(statusService.getGeneralStats());
        allStats.setMovies(statusService.getMovieStats());
        allStats.setSeries(statusService.getSerieStats());
        allStats.setAnimes(statusService.getAnimeStats());
        allStats.setTvs(statusService.getTvStats());
        allStats.setUsers(statusService.getUserStats());

        return ResponseEntity.ok(allStats);
    }
}