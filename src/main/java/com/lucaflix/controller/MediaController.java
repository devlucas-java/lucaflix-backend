package com.lucaflix.controller;

import com.lucaflix.dto.media.MediaDTO;
import com.lucaflix.model.User;
import com.lucaflix.model.enums.Categoria;
import com.lucaflix.security.CurrentUser;
import com.lucaflix.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@Tag(name = "Media", description = "Endpoints para gerenciamento de mídia (filmes e séries)")
public class MediaController {

    private final MediaService mediaService;

    /// BUSCA FILME PARA SINGLE PAGE DO FRONTEND
    @GetMapping("/filmes/{filmeId}")
    @Operation(summary = "Obter detalhes do filme", description = "Retorna informações detalhadas de um filme específico")
    public ResponseEntity<MediaDTO.FilmeDetailsResponse> getFilmeById(
            @Parameter(description = "ID do filme") @PathVariable Long filmeId) {
        try {
            MediaDTO.FilmeDetailsResponse filme = mediaService.getFilmeById(filmeId);
            return ResponseEntity.ok(filme);
        } catch (Exception e) {
            log.error("Erro ao buscar filme {}: {}", filmeId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /// BUSCA SERIE E SUAS TEMPORADAS E EPISODIOS PARA SINGLE PAGE DO FRONTEND
    @GetMapping("/series/{serieId}")
    @Operation(summary = "Obter detalhes da série", description = "Retorna informações detalhadas de uma série com temporadas e episódios")
    public ResponseEntity<MediaDTO.SerieDetailsResponse> getSerieById(
            @Parameter(description = "ID da série") @PathVariable Long serieId) {
        try {
            MediaDTO.SerieDetailsResponse serie = mediaService.getSerieById(serieId);
            return ResponseEntity.ok(serie);
        } catch (Exception e) {
            log.error("Erro ao buscar série {}: {}", serieId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /// BUSCA TOP 10 SERIES COM MAIS LIKES
    @GetMapping("/series/top-likes")
    @Operation(summary = "Top 10 séries mais curtidas", description = "Retorna as 10 séries com mais likes")
    public ResponseEntity<List<MediaDTO.SerieResponse>> getTop10SeriesByLikes() {
        try {
            List<MediaDTO.SerieResponse> topSeries = mediaService.getTop10SeriesByLikes();
            return ResponseEntity.ok(topSeries);
        } catch (Exception e) {
            log.error("Erro ao buscar top séries: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /// BUSCA TOP 10 FILMES COM MAIS LIKES
    @GetMapping("/filmes/top-likes")
    @Operation(summary = "Top 10 filmes mais curtidos", description = "Retorna os 10 filmes com mais likes")
    public ResponseEntity<List<MediaDTO.FilmeResponse>> getTop10FilmesByLikes() {
        try {
            List<MediaDTO.FilmeResponse> topFilmes = mediaService.getTop10FilmesByLikes();
            return ResponseEntity.ok(topFilmes);
        } catch (Exception e) {
            log.error("Erro ao buscar top filmes: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /// BUSCA SERIES E FILMES QUE O USER MARCOU COMO ASSISTIDO OU ESTA ASSISTINDO
    @GetMapping("/my-watched-content")
    @Operation(summary = "Meu conteúdo assistido", description = "Retorna filmes assistidos e séries sendo assistidas pelo usuário")
    public ResponseEntity<MediaDTO.UserWatchedContentResponse> getUserWatchedContent(@CurrentUser User user) {
        try {
            MediaDTO.UserWatchedContentResponse watchedContent = mediaService.getUserWatchedContent(user);
            return ResponseEntity.ok(watchedContent);
        } catch (Exception e) {
            log.error("Erro ao buscar conteúdo assistido do usuário {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /// BUSCA MINHA LISTA
    @GetMapping("/my-list")
    @Operation(summary = "Minha lista", description = "Retorna a lista completa de filmes e séries do usuário")
    public ResponseEntity<MediaDTO.MinhaListaResponse> getMinhaLista(@CurrentUser User user) {
        try {
            MediaDTO.MinhaListaResponse minhaLista = mediaService.getMinhaLista(user);
            return ResponseEntity.ok(minhaLista);
        } catch (Exception e) {
            log.error("Erro ao buscar minha lista do usuário {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /// SISTEMA DE FILTRAR POR SERIES, FILMES E CATEGORIA
    @GetMapping("/filter")
    @Operation(summary = "Filtrar conteúdo", description = "Filtra conteúdo por tipo (filme/serie/todos) e categoria")
    public ResponseEntity<MediaDTO.FilteredContentResponse> filterContent(
            @Parameter(description = "Tipo de conteúdo (filme, serie, todos)") @RequestParam(defaultValue = "todos") String tipo,
            @Parameter(description = "Categoria do conteúdo") @RequestParam(required = false) Categoria categoria,
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size) {
        try {
            MediaDTO.FilteredContentResponse filteredContent = mediaService.filterContent(tipo, categoria, page, size);
            return ResponseEntity.ok(filteredContent);
        } catch (Exception e) {
            log.error("Erro ao filtrar conteúdo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /// SISTEMA DE BUSCAR SERIE OU FILMES POR NOME OU PARTE DO NOME
    @GetMapping("/search")
    @Operation(summary = "Buscar conteúdo", description = "Busca filmes e séries por nome ou parte do nome")
    public ResponseEntity<MediaDTO.SearchResultResponse> searchContent(
            @Parameter(description = "Termo de busca") @RequestParam String q) {
        try {
            if (q == null || q.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            MediaDTO.SearchResultResponse searchResults = mediaService.searchContent(q.trim());
            return ResponseEntity.ok(searchResults);
        } catch (Exception e) {
            log.error("Erro ao buscar conteúdo com termo '{}': {}", q, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /// ADICIONA OU REMOVE LIKE DE FILME
    @PostMapping("/filmes/{filmeId}/like")
    @Operation(summary = "Curtir/Descurtir filme", description = "Adiciona ou remove like de um filme")
    public ResponseEntity<MediaDTO.LikeResponse> toggleFilmeLike(
            @Parameter(description = "ID do filme") @PathVariable Long filmeId,
            @CurrentUser User user) {
        try {
            MediaDTO.LikeResponse likeResponse = mediaService.toggleFilmeLike(user, filmeId);
            return ResponseEntity.ok(likeResponse);
        } catch (Exception e) {
            log.error("Erro ao alterar like do filme {} para usuário {}: {}", filmeId, user.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /// ADICIONA OU REMOVE LIKE DE SERIE
    @PostMapping("/series/{serieId}/like")
    @Operation(summary = "Curtir/Descurtir série", description = "Adiciona ou remove like de uma série")
    public ResponseEntity<MediaDTO.LikeResponse> toggleSerieLike(
            @Parameter(description = "ID da série") @PathVariable Long serieId,
            @CurrentUser User user) {
        try {
            MediaDTO.LikeResponse likeResponse = mediaService.toggleSerieLike(user, serieId);
            return ResponseEntity.ok(likeResponse);
        } catch (Exception e) {
            log.error("Erro ao alterar like da série {} para usuário {}: {}", serieId, user.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /// VERIFICA SE USUARIO JA CURTIU O FILME
    @GetMapping("/filmes/{filmeId}/like-status")
    @Operation(summary = "Status do like do filme", description = "Verifica se o usuário já curtiu o filme")
    public ResponseEntity<Map<String, Boolean>> getFilmeLikeStatus(
            @Parameter(description = "ID do filme") @PathVariable Long filmeId,
            @CurrentUser User user) {
        try {
            boolean liked = mediaService.hasUserLikedFilme(user, filmeId);
            return ResponseEntity.ok(Map.of("liked", liked));
        } catch (Exception e) {
            log.error("Erro ao verificar like do filme {} para usuário {}: {}", filmeId, user.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /// VERIFICA SE USUARIO JA CURTIU A SERIE
    @GetMapping("/series/{serieId}/like-status")
    @Operation(summary = "Status do like da série", description = "Verifica se o usuário já curtiu a série")
    public ResponseEntity<Map<String, Boolean>> getSerieLikeStatus(
            @Parameter(description = "ID da série") @PathVariable Long serieId,
            @CurrentUser User user) {
        try {
            boolean liked = mediaService.hasUserLikedSerie(user, serieId);
            return ResponseEntity.ok(Map.of("liked", liked));
        } catch (Exception e) {
            log.error("Erro ao verificar like da série {} para usuário {}: {}", serieId, user.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}