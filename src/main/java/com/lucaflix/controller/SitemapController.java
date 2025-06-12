package com.lucaflix.controller;

import com.lucaflix.model.Media;
import com.lucaflix.service.SitemapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SitemapController {

    private final SitemapService sitemapService;

    /**
     * Endpoint para gerar e servir o sitemap.xml
     * URL: GET /sitemap.xml
     */
    @GetMapping(value = "/api/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getSitemap() {
        try {
            log.info("Gerando sitemap XML...");
            String sitemapXml = sitemapService.generateSitemapXml();

            return ResponseEntity.ok()
                    .header("Content-Type", "application/xml; charset=UTF-8")
                    .header("Cache-Control", "public, max-age=3600") // Cache por 1 hora
                    .body(sitemapXml);

        } catch (Exception e) {
            log.error("Erro ao gerar sitemap: ", e);
            return ResponseEntity.internalServerError()
                    .body("<?xml version=\"1.0\" encoding=\"UTF-8\"?><error>Erro ao gerar sitemap</error>");
        }
    }

    /**
     * Endpoint para obter URLs do sitemap em formato JSON (para debug)
     * URL: GET /api/sitemap/urls
     */
    @GetMapping("/api/sitemap/urls")
    public ResponseEntity<?> getSitemapUrls() {
        try {
            return ResponseEntity.ok(sitemapService.generateSitemapUrls());
        } catch (Exception e) {
            log.error("Erro ao gerar URLs do sitemap: ", e);
            return ResponseEntity.internalServerError()
                    .body("Erro ao gerar URLs do sitemap");
        }
    }
}