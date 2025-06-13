package com.lucaflix.controller;

import com.lucaflix.service.SitemapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SitemapController {

    private final SitemapService sitemapService;

    /**
     * Endpoint principal para servir o sitemap.xml
     * URL: GET /sitemap.xml
     */
    @GetMapping(value = "/api/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getSitemap() {
        try {
            log.info("Requisição recebida para sitemap.xml");

            // Valida o sitemap antes de servir
            if (!sitemapService.validateSitemapUrls()) {
                log.warn("Sitemap inválido detectado");
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("<?xml version=\"1.0\" encoding=\"UTF-8\"?><error>Sitemap temporariamente indisponível</error>");
            }

            String sitemapXml = sitemapService.generateSitemapXml();

            log.info("Sitemap.xml gerado com sucesso ({} bytes)", sitemapXml.length());

            return ResponseEntity.ok()
                    .header("Content-Type", "application/xml; charset=UTF-8")
                    .header("Cache-Control", "public, max-age=3600") // Cache por 1 hora
                    .header("X-Robots-Tag", "noindex") // Evita indexação do próprio sitemap
                    .body(sitemapXml);

        } catch (Exception e) {
            log.error("Erro ao gerar sitemap.xml: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("<?xml version=\"1.0\" encoding=\"UTF-8\"?><error>Erro interno do servidor</error>");
        }
    }

    /**
     * Endpoint alternativo com prefixo /api
     * URL: GET /api/sitemap.xml
     */
    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getApiSitemap() {
        return getSitemap();
    }

    /**
     * Endpoint para sitemap index (se necessário múltiplos sitemaps)
     * URL: GET /sitemap-index.xml
     */
    @GetMapping(value = "/sitemap-index.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getSitemapIndex() {
        try {
            log.info("Requisição recebida para sitemap-index.xml");

            String sitemapIndex = sitemapService.generateSitemapIndex();

            return ResponseEntity.ok()
                    .header("Content-Type", "application/xml; charset=UTF-8")
                    .header("Cache-Control", "public, max-age=7200") // Cache por 2 horas
                    .body(sitemapIndex);

        } catch (Exception e) {
            log.error("Erro ao gerar sitemap-index.xml: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("<?xml version=\"1.0\" encoding=\"UTF-8\"?><error>Erro ao gerar sitemap index</error>");
        }
    }

    /**
     * Endpoint para obter URLs do sitemap em formato JSON (para debug/admin)
     * URL: GET /api/sitemap/urls
     */
    @GetMapping("/api/sitemap/urls")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getSitemapUrls() {
        try {
            log.info("Requisição para URLs do sitemap (formato JSON)");

            var urls = sitemapService.generateSitemapUrls();

            return ResponseEntity.ok()
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .body(Map.of(
                            "total", urls.size(),
                            "urls", urls,
                            "generated_at", java.time.LocalDateTime.now()
                    ));

        } catch (Exception e) {
            log.error("Erro ao gerar URLs do sitemap: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao gerar URLs do sitemap"));
        }
    }

    /**
     * Endpoint para validar sitemap (admin)
     * URL: GET /api/sitemap/validate
     */
    @GetMapping("/api/sitemap/validate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> validateSitemap() {
        try {
            log.info("Validação do sitemap solicitada");

            boolean isValid = sitemapService.validateSitemapUrls();
            var urls = sitemapService.generateSitemapUrls();

            return ResponseEntity.ok(Map.of(
                    "valid", isValid,
                    "total_urls", urls.size(),
                    "validation_time", java.time.LocalDateTime.now(),
                    "status", isValid ? "OK" : "INVALID"
            ));

        } catch (Exception e) {
            log.error("Erro ao validar sitemap: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao validar sitemap"));
        }
    }

    /**
     * Endpoint para limpar cache do sitemap (admin)
     * URL: POST /api/sitemap/clear-cache
     */
    @PostMapping("/api/sitemap/clear-cache")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> clearSitemapCache() {
        try {
            log.info("Limpeza de cache do sitemap solicitada");

            sitemapService.clearSitemapCache();

            return ResponseEntity.ok(Map.of(
                    "message", "Cache do sitemap limpo com sucesso",
                    "cleared_at", java.time.LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("Erro ao limpar cache do sitemap: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao limpar cache do sitemap"));
        }
    }

    /**
     * Endpoint para estatísticas do sitemap (admin)
     * URL: GET /api/sitemap/stats
     */
    @GetMapping("/api/sitemap/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getSitemapStats() {
        try {
            log.info("Estatísticas do sitemap solicitadas");

            var urls = sitemapService.generateSitemapUrls();

            // Agrupa URLs por tipo
            long staticUrls = urls.stream()
                    .filter(url -> !url.getLoc().contains("/filme/") && !url.getLoc().contains("/serie/"))
                    .count();

            long movieUrls = urls.stream()
                    .filter(url -> url.getLoc().contains("/filme/"))
                    .count();

            long serieUrls = urls.stream()
                    .filter(url -> url.getLoc().contains("/serie/"))
                    .count();

            return ResponseEntity.ok(Map.of(
                    "total_urls", urls.size(),
                    "static_urls", staticUrls,
                    "movie_urls", movieUrls,
                    "serie_urls", serieUrls,
                    "generated_at", java.time.LocalDateTime.now(),
                    "estimated_size_kb", (sitemapService.generateSitemapXml().length() / 1024)
            ));

        } catch (Exception e) {
            log.error("Erro ao gerar estatísticas do sitemap: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao gerar estatísticas"));
        }
    }
}