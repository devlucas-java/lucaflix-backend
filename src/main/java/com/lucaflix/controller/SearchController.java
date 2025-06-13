package com.lucaflix.controller;

import com.lucaflix.dto.media.PaginatedResponseDTO;
import com.lucaflix.dto.media.SearchResultDTO;
import com.lucaflix.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * Buscar filmes e séries por múltiplos critérios
     * @param texto - busca no título e sinopse (opcional)
     * @param categoria - filtro por categoria (opcional)
     * @param tipo - movie, serie ou all (opcional, padrão: all)
     */
    @GetMapping("/media")
    public ResponseEntity<PaginatedResponseDTO<SearchResultDTO>> searchMedia(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false, defaultValue = "all") String tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<SearchResultDTO> response = searchService.searchMedia(texto, categoria, tipo, page, size);
        return ResponseEntity.ok(response);
    }
}