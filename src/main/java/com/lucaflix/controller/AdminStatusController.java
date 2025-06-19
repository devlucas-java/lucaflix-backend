package com.lucaflix.controller;

import com.lucaflix.dto.admin.stats.DetailedStatsDTO;
import com.lucaflix.dto.admin.stats.MediaStatsDTO;
import com.lucaflix.service.AdminAnimeService;
import com.lucaflix.service.AdminMovieService;
import com.lucaflix.service.AdminSerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/api/admin/status")
public class AdminStatusController {

    @Autowired
    private AdminMovieService adminMovieService;
    @Autowired
    private AdminAnimeService adminAnimeService;
    @Autowired
    private AdminSerieService adminSerieService;

    // ==================== ESTATÍSTICAS FILME ====================

    @GetMapping("/stats/filme")
    public ResponseEntity<MediaStatsDTO> getCompleteStatsFimle() {
        MediaStatsDTO stats = adminMovieService.getCompleteStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/filme/detailed")
    public ResponseEntity<DetailedStatsDTO> getDetailedStatsFilme() {
        DetailedStatsDTO stats = adminMovieService.getDetailedStats();
        return ResponseEntity.ok(stats);
    }

    // ==================== ESTATÍSTICAS ANIME ====================

    @GetMapping("/stats/anime")
    public ResponseEntity<MediaStatsDTO> getCompleteStatsAnime() {
        MediaStatsDTO stats = adminAnimeService.getCompleteStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/anime/detailed")
    public ResponseEntity<DetailedStatsDTO> getDetailedStatsAnime() {
        DetailedStatsDTO stats = adminAnimeService.getDetailedStats();
        return ResponseEntity.ok(stats);
    }
}
