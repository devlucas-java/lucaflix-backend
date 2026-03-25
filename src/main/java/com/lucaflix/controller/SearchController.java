package com.lucaflix.controller;

import com.lucaflix.dto.response.page.PaginatedResponseDTO;
import com.lucaflix.security.SkipJwtAuthentication;
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
     * Buscar filmes, séries e animes por múltiplos critérios
     * @param texto - busca no título e sinopse (opcional)
     * @param categoria - filtro por categoria (opcional)
     * @param tipo - movie, serie, anime ou all (opcional, padrão: all)
     * @param page - número da página (padrão: 0)
     * @param size - tamanho da página (padrão: 20)
     */
    @GetMapping("/media")
    @SkipJwtAuthentication
    public ResponseEntity<PaginatedResponseDTO<Object>> searchMedia(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false, defaultValue = "all") String tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<Object> response = searchService.searchMedia(texto, categoria, tipo, page, size);
        return ResponseEntity.ok(response);
    }
}