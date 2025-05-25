package com.lucaflix.controller;

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

    // ENDPOINTS DE FILMES

    /**
     * Endpoint para adicionar um novo filme ao catálogo
     */
    @PostMapping("/filmes")
    public ResponseEntity<Filme> adicionarFilme(@RequestBody Filme filme, @CurrentUser User currentUser) {
        try {
            if (!adminService.isAdmin(currentUser)) {
                return ResponseEntity.status(403).build();
            }

            Filme novoFilme = adminService.adicionarFilme(filme);
            return ResponseEntity.status(201).body(novoFilme);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint para atualizar dados de um filme existente
     */
    @PutMapping("/filmes/{id}")
    public ResponseEntity<Filme> atualizarFilme(@PathVariable Long id, @RequestBody Filme filmeAtualizado, @CurrentUser User currentUser) {
        try {
            if (!adminService.isAdmin(currentUser)) {
                return ResponseEntity.status(403).build();
            }

            Filme filme = adminService.atualizarFilme(id, filmeAtualizado);

            if (filme != null) {
                return ResponseEntity.ok(filme);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint para excluir um filme específico do catálogo
     */
    @DeleteMapping("/filmes/{id}")
    public ResponseEntity<Void> excluirFilme(@PathVariable Long id, @CurrentUser User currentUser) {
        try {
            if (!adminService.isAdmin(currentUser)) {
                return ResponseEntity.status(403).build();
            }

            boolean excluido = adminService.excluirFilme(id);

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
     * Endpoint para listar todos os filmes (para administração)
     */
    @GetMapping("/filmes")
    public ResponseEntity<List<Filme>> listarFilmes(@CurrentUser User currentUser) {
        try {
            if (!adminService.isAdmin(currentUser)) {
                return ResponseEntity.status(403).build();
            }

            List<Filme> filmes = adminService.listarFilmes();
            return ResponseEntity.ok(filmes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ENDPOINTS DE SÉRIES

    /**
     * Endpoint para adicionar uma nova série ao catálogo
     */
    @PostMapping("/series")
    public ResponseEntity<Serie> adicionarSerie(@RequestBody Serie serie, @CurrentUser User currentUser) {
        try {
            if (!adminService.isAdmin(currentUser)) {
                return ResponseEntity.status(403).build();
            }

            Serie novaSerie = adminService.adicionarSerie(serie);
            return ResponseEntity.status(201).body(novaSerie);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint para atualizar dados de uma série existente
     */
    @PutMapping("/series/{id}")
    public ResponseEntity<Serie> atualizarSerie(@PathVariable Long id, @RequestBody Serie serieAtualizada, @CurrentUser User currentUser) {
        try {
            if (!adminService.isAdmin(currentUser)) {
                return ResponseEntity.status(403).build();
            }

            Serie serie = adminService.atualizarSerie(id, serieAtualizada);

            if (serie != null) {
                return ResponseEntity.ok(serie);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint para excluir uma série específica do catálogo
     */
    @DeleteMapping("/series/{id}")
    public ResponseEntity<Void> excluirSerie(@PathVariable Long id, @CurrentUser User currentUser) {
        try {
            if (!adminService.isAdmin(currentUser)) {
                return ResponseEntity.status(403).build();
            }

            boolean excluido = adminService.excluirSerie(id);

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
     * Endpoint para listar todas as séries (para administração)
     */
    @GetMapping("/series")
    public ResponseEntity<List<Serie>> listarSeries(@CurrentUser User currentUser) {
        try {
            if (!adminService.isAdmin(currentUser)) {
                return ResponseEntity.status(403).build();
            }

            List<Serie> series = adminService.listarSeries();
            return ResponseEntity.ok(series);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ENDPOINTS DE TEMPORADAS

    /**
     * Endpoint para adicionar uma nova temporada a uma série existente
     */
    @PostMapping("/series/{serieId}/temporadas")
    public ResponseEntity<Temporada> adicionarTemporada(@PathVariable Long serieId, @RequestBody Temporada temporada, @CurrentUser User currentUser) {
        try {
            if (!adminService.isAdmin(currentUser)) {
                return ResponseEntity.status(403).build();
            }

            Temporada novaTemporada = adminService.adicionarTemporada(serieId, temporada);

            if (novaTemporada != null) {
                return ResponseEntity.status(201).body(novaTemporada);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint para atualizar dados de uma temporada existente
     */
    @PutMapping("/temporadas/{id}")
    public ResponseEntity<Temporada> atualizarTemporada(@PathVariable Long id, @RequestBody Temporada temporadaAtualizada, @CurrentUser User currentUser) {
        try {
            if (!adminService.isAdmin(currentUser)) {
                return ResponseEntity.status(403).build();
            }

            Temporada temporada = adminService.atualizarTemporada(id, temporadaAtualizada);

            if (temporada != null) {
                return ResponseEntity.ok(temporada);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint para excluir uma temporada específica
     */
    @DeleteMapping("/temporadas/{id}")
    public ResponseEntity<Void> excluirTemporada(@PathVariable Long id, @CurrentUser User currentUser) {
        try {
            if (!adminService.isAdmin(currentUser)) {
                return ResponseEntity.status(403).build();
            }

            boolean excluido = adminService.excluirTemporada(id);

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
     * Endpoint para listar todas as temporadas de uma série específica
     */
    @GetMapping("/series/{serieId}/temporadas")
    public ResponseEntity<List<Temporada>> listarTemporadasDaSerie(@PathVariable Long serieId, @CurrentUser User currentUser) {
        try {
            if (!adminService.isAdmin(currentUser)) {
                return ResponseEntity.status(403).build();
            }

            List<Temporada> temporadas = adminService.listarTemporadasDaSerie(serieId);

            if (temporadas != null) {
                return ResponseEntity.ok(temporadas);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ENDPOINTS DE EPISÓDIOS

    /**
     * Endpoint para adicionar um episódio a uma temporada específica
     */
    @PostMapping("/temporadas/{temporadaId}/episodios")
    public ResponseEntity<Episodio> adicionarEpisodio(@PathVariable Long temporadaId, @RequestBody Episodio episodio, @CurrentUser User currentUser) {
        try {
            if (!adminService.isAdmin(currentUser)) {
                return ResponseEntity.status(403).build();
            }

            Episodio novoEpisodio = adminService.adicionarEpisodio(temporadaId, episodio);

            if (novoEpisodio != null) {
                return ResponseEntity.status(201).body(novoEpisodio);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint para atualizar dados de um episódio existente
     */
    @PutMapping("/episodios/{id}")
    public ResponseEntity<Episodio> atualizarEpisodio(@PathVariable Long id, @RequestBody Episodio episodioAtualizado, @CurrentUser User currentUser) {
        try {
            if (!adminService.isAdmin(currentUser)) {
                return ResponseEntity.status(403).build();
            }

            Episodio episodio = adminService.atualizarEpisodio(id, episodioAtualizado);

            if (episodio != null) {
                return ResponseEntity.ok(episodio);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Endpoint para excluir um episódio específico de uma temporada
     */
    @DeleteMapping("/episodios/{id}")
    public ResponseEntity<Void> excluirEpisodio(@PathVariable Long id, @CurrentUser User currentUser) {
        try {
            if (!adminService.isAdmin(currentUser)) {
                return ResponseEntity.status(403).build();
            }

            boolean excluido = adminService.excluirEpisodio(id);

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
     * Endpoint para listar todos os episódios de uma temporada específica
     */
    @GetMapping("/temporadas/{temporadaId}/episodios")
    public ResponseEntity<List<Episodio>> listarEpisodiosDaTemporada(@PathVariable Long temporadaId, @CurrentUser User currentUser) {
        try {
            if (!adminService.isAdmin(currentUser)) {
                return ResponseEntity.status(403).build();
            }

            List<Episodio> episodios = adminService.listarEpisodiosDaTemporada(temporadaId);

            if (episodios != null) {
                return ResponseEntity.ok(episodios);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}