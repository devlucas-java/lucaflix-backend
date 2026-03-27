package com.lucaflix.controller;

import com.lucaflix.service.SitemapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SitemapController {

    private final SitemapService sitemapService;

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getSitemap() {

        log.info("Gerando sitemap.xml");

        String xml = sitemapService.generateSitemapXml();

        return ResponseEntity.ok()
                .header("Content-Type", "application/xml; charset=UTF-8")
                .header("Cache-Control", "public, max-age=3600")
                .body(xml);
    }
}