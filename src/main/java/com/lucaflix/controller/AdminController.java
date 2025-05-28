package com.lucaflix.controller;

import com.lucaflix.dto.admin.AdminMediaDTO;
import com.lucaflix.dto.admin.CreateMediaDTO;
import com.lucaflix.dto.admin.MediaStatsDTO;
import com.lucaflix.dto.admin.UpdateMediaDTO;
import com.lucaflix.dto.media.PaginatedResponseDTO;
import com.lucaflix.model.*;
import com.lucaflix.service.AdminService;
import com.lucaflix.security.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Endpoint para listar todas as mídias
     */
    @GetMapping("/medias")
    public PaginatedResponseDTO<AdminMediaDTO> listarTodasAsMedias(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return adminService.getAllMediaForAdmin(page, size);
    }

    /**
     * Endpoint para adicionar uma nova mídia ao catálogo
     */
    @PostMapping("/medias")
    public ResponseEntity<AdminMediaDTO> adicionarMedia(@RequestBody CreateMediaDTO createMediaDTO, @CurrentUser User currentUser) {
        try {
            AdminMediaDTO novoMedia = adminService.createMedia(createMediaDTO);
            return ResponseEntity.status(201).body(novoMedia);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint para atualizar dados de uma mídia existente
     */
    @PutMapping("/medias/{id}")
    public ResponseEntity<AdminMediaDTO> atualizarMedia(
            @PathVariable Long id,
            @RequestBody UpdateMediaDTO updateMediaDTO,
            @CurrentUser User currentUser) {
        try {
            AdminMediaDTO media = adminService.updateMedia(id, updateMediaDTO);

            if (media != null) {
                return ResponseEntity.ok(media);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint para excluir uma mídia específica do catálogo
     */
    @DeleteMapping("/medias/{id}")
    public ResponseEntity<Void> excluirMedia(
            @PathVariable Long id,
            @CurrentUser User currentUser) {
        try {
            boolean excluido = adminService.deleteMedia(id);

            if (excluido) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint para listar todos os filmes no catálogo
     */
    @GetMapping("/filmes")
    public PaginatedResponseDTO<AdminMediaDTO> listarFilmes(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return adminService.getFilmesForAdmin(page, size);
    }

    /**
     * Endpoint para listar todas as séries no catálogo
     */
    @GetMapping("/series")
    public PaginatedResponseDTO<AdminMediaDTO> listarSeries(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return adminService.getSeriesForAdmin(page, size);
    }

    /**
     * Endpoint para obter estatísticas gerais das mídias
     */
    @GetMapping("/estatisticas")
    public MediaStatsDTO getStats() {
        return adminService.getMediaStats();
    }

    /**
     * Endpoint para atualizar a avaliação de uma mídia
     */
    @PutMapping("/medias/{id}/avaliacao")
    public ResponseEntity<AdminMediaDTO> updateMediaRating(
            @PathVariable Long id,
            @RequestParam double rating) {
        try {
            AdminMediaDTO updatedMedia = adminService.updateMediaRating(id, rating);
            return ResponseEntity.ok(updatedMedia);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}