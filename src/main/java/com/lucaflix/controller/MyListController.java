package com.lucaflix.controller;

import com.lucaflix.dto.media.PaginatedResponseDTO;
import com.lucaflix.model.User;
import com.lucaflix.security.CurrentUser;
import com.lucaflix.service.MyListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/my-list")
@RequiredArgsConstructor
public class MyListController {

    private final MyListService myListService;

    /**
     * Obter todos os itens da lista do usuário (filmes, séries e animes)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PaginatedResponseDTO<Object>> getMyList(
            @CurrentUser User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<Object> response = myListService.getMyList(currentUser.getId(), page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Obter apenas filmes da lista do usuário
     */
    @GetMapping("/movies")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PaginatedResponseDTO<Object>> getMyListMovies(
            @CurrentUser User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<Object> response = myListService.getMyListByType(currentUser.getId(), "MOVIE", page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Obter apenas séries da lista do usuário
     */
    @GetMapping("/series")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PaginatedResponseDTO<Object>> getMyListSeries(
            @CurrentUser User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<Object> response = myListService.getMyListByType(currentUser.getId(), "SERIE", page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Obter apenas animes da lista do usuário
     */
    @GetMapping("/animes")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PaginatedResponseDTO<Object>> getMyListAnimes(
            @CurrentUser User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<Object> response = myListService.getMyListByType(currentUser.getId(), "ANIME", page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint unificado com parâmetro de filtro por tipo
     */
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PaginatedResponseDTO<Object>> getMyListFiltered(
            @CurrentUser User currentUser,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PaginatedResponseDTO<Object> response;

        if (type == null || type.isEmpty()) {
            response = myListService.getMyList(currentUser.getId(), page, size);
        } else {
            response = myListService.getMyListByType(currentUser.getId(), type.toUpperCase(), page, size);
        }

        return ResponseEntity.ok(response);
    }
}