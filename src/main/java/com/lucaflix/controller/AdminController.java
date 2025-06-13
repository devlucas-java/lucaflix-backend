//package com.lucaflix.controller;
//
//import com.lucaflix.dto.admin.CreateMovieDTO;
//import com.lucaflix.dto.admin.stats.MediaStatsDTO;
//import com.lucaflix.dto.admin.UpdateMovieDTO;
//import com.lucaflix.model.*;
//import com.lucaflix.service.AdminService;
//import com.lucaflix.security.CurrentUser;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/admin")
//@PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
//public class AdminController {
//
//    @Autowired
//    private AdminService adminService;
//
//
//    /**
//     * Endpoint para adicionar uma nova mídia ao catálogo
//     */
//    @PostMapping("/medias")
//    public ResponseEntity<AdminMovieDTO> adicionarMedia(@RequestBody CreateMovieDTO createMovieDTO, @CurrentUser User currentUser) {
//        try {
//            AdminMovieDTO novoMedia = adminService.createMedia(createMovieDTO);
//            return ResponseEntity.status(201).body(novoMedia);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    /**
//     * Endpoint para atualizar dados de uma mídia existente
//     */
//    @PutMapping("/medias/{id}")
//    public ResponseEntity<AdminMovieDTO> atualizarMedia(
//            @PathVariable Long id,
//            @RequestBody UpdateMovieDTO updateMovieDTO,
//            @CurrentUser User currentUser) {
//        try {
//            AdminMovieDTO media = adminService.updateMedia(id, updateMovieDTO);
//
//            if (media != null) {
//                return ResponseEntity.ok(media);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    /**
//     * Endpoint para excluir uma mídia específica do catálogo
//     */
//    @DeleteMapping("/medias/{id}")
//    public ResponseEntity<Void> excluirMedia(
//            @PathVariable Long id,
//            @CurrentUser User currentUser) {
//        try {
//            boolean excluido = adminService.deleteMedia(id);
//
//            if (excluido) {
//                return ResponseEntity.noContent().build();
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//    /**
//     * Endpoint para obter estatísticas gerais das mídias
//     */
//    @GetMapping("/estatisticas")
//    public MediaStatsDTO getStats() {
//        return adminService.getMediaStats();
//    }
//}