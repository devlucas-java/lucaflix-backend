package com.lucaflix.controller;

import com.lucaflix.dto.admin.AdminMediaDTO;
import com.lucaflix.dto.admin.CreateMediaDTO;
import com.lucaflix.dto.admin.MediaStatsDTO;
import com.lucaflix.dto.admin.UpdateMediaDTO;
import com.lucaflix.dto.media.MediaFilter;
import com.lucaflix.dto.media.MediaSimpleDTO;
import com.lucaflix.dto.media.PaginatedResponseDTO;
import com.lucaflix.model.*;
import com.lucaflix.model.enums.Categoria;
import com.lucaflix.service.AdminService;
import com.lucaflix.security.CurrentUser;
import com.lucaflix.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private MediaService mediaService;

    /**
     * Lista todas as mídias com paginação
     */
    @GetMapping("/search")
    public ResponseEntity<PaginatedResponseDTO<MediaSimpleDTO>> buscarMediaPorFiltro(
            @RequestParam(required = false) Boolean isFilme,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Double avaliacao,
            @RequestParam(required = false) String anoLancamentoInicio,
            @RequestParam(required = false) String anoLancamentoFim,
            @RequestParam(required = false) String categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        MediaFilter filter = new MediaFilter();
        filter.setIsFilme(isFilme);
        filter.setTitle(title);
        filter.setAvaliacao(avaliacao);

        if (categoria != null) {
            filter.setCategoria(Categoria.valueOf(categoria.toUpperCase()));
        }

        PaginatedResponseDTO<MediaSimpleDTO> response = mediaService.filtrarMedia(filter, page, size);
        return ResponseEntity.ok(response);
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
     * Endpoint para obter estatísticas gerais das mídias
     */
    @GetMapping("/estatisticas")
    public MediaStatsDTO getStats() {
        return adminService.getMediaStats();
    }
}