//package com.lucaflix.service;
//
//import com.lucaflix.dto.SitemapUrlDto;
//import com.lucaflix.model.Movie;
//import com.lucaflix.repository.MovieRepository;
//import com.lucaflix.service.utils.UrlFormatter;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class SitemapService {
//
//    private final MovieRepository movieRepository;
//    private final UrlFormatter urlFormatter;
//
//    @Value("${app.base-url:https://lucaflix.com}")
//    private String baseUrl;
//
//    /**
//     * Gera todas as URLs para o sitemap
//     */
//    public List<SitemapUrlDto> generateSitemapUrls() {
//        List<SitemapUrlDto> urls = new ArrayList<>();
//
//        try {
//            // URLs estáticas do site
//            urls.addAll(getStaticUrls());
//
//            // URLs dinâmicas das mídias
//            urls.addAll(getMediaUrls());
//
//            log.info("Sitemap gerado com {} URLs", urls.size());
//
//        } catch (Exception e) {
//            log.error("Erro ao gerar sitemap: ", e);
//        }
//
//        return urls;
//    }
//
//    /**
//     * URLs estáticas do site
//     */
//    private List<SitemapUrlDto> getStaticUrls() {
//        List<SitemapUrlDto> staticUrls = new ArrayList<>();
//        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
//
//        // Página inicial
//        staticUrls.add(new SitemapUrlDto(
//                baseUrl + "/",
//                now,
//                "daily",
//                "1.0"
//        ));
//
//        // Páginas principais
//        staticUrls.add(new SitemapUrlDto(
//                baseUrl + "/filmes",
//                now,
//                "daily",
//                "0.9"
//        ));
//
//        staticUrls.add(new SitemapUrlDto(
//                baseUrl + "/series",
//                now,
//                "daily",
//                "0.9"
//        ));
//
//        staticUrls.add(new SitemapUrlDto(
//                baseUrl + "/categorias",
//                now,
//                "weekly",
//                "0.8"
//        ));
//
//        staticUrls.add(new SitemapUrlDto(
//                baseUrl + "/minha-lista",
//                now,
//                "daily",
//                "0.7"
//        ));
//
//        // Páginas por categoria
//        String[] categorias = {
//                "acao", "comedia", "drama", "terror", "suspense",
//                "romance", "ficcao-cientifica", "aventura", "fantasia",
//                "documentario", "teen", "reality"
//        };
//
//        for (String categoria : categorias) {
//            staticUrls.add(new SitemapUrlDto(
//                    baseUrl + "/categoria/" + categoria,
//                    now,
//                    "weekly",
//                    "0.6"
//            ));
//        }
//
//        return staticUrls;
//    }
//
//    /**
//     * URLs das mídias (filmes e séries)
//     */
//    private List<SitemapUrlDto> getMediaUrls() {
//        List<SitemapUrlDto> mediaUrls = new ArrayList<>();
//
//        try {
//            List<Movie> allMovie = movieRepository.findAll();
//
//            for (Movie movie : allMovie) {
//                if (movie.getTitle() != null && !movie.getTitle().trim().isEmpty()) {
//                    String urlPath = urlFormatter.formatTitleForUrl(
//                            movie.getId(),
//                            movie.getTitle(),
//                            movie.getAnoLancamento(),
//                            movie.isFilme()
//                    );
//
//                    String lastmod = movie.getDataCadastro() != null
//                            ? movie.getDataCadastro().toInstant()
//                            .atZone(java.time.ZoneId.systemDefault())
//                            .toLocalDate()
//                            .format(DateTimeFormatter.ISO_LOCAL_DATE)
//                            : LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
//
//                    mediaUrls.add(new SitemapUrlDto(
//                            baseUrl + urlPath,
//                            lastmod,
//                            "monthly",
//                            "0.8"
//                    ));
//                }
//            }
//
//        } catch (Exception e) {
//            log.error("Erro ao buscar mídias para sitemap: ", e);
//        }
//
//        return mediaUrls;
//    }
//
//    /**
//     * Gera o XML do sitemap
//     */
//    public String generateSitemapXml() {
//        List<SitemapUrlDto> urls = generateSitemapUrls();
//
//        StringBuilder xml = new StringBuilder();
//        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
//        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
//
//        for (SitemapUrlDto url : urls) {
//            xml.append("  <url>\n");
//            xml.append("    <loc>").append(escapeXml(url.getLoc())).append("</loc>\n");
//            xml.append("    <lastmod>").append(url.getLastmod()).append("</lastmod>\n");
//            xml.append("    <changefreq>").append(url.getChangefreq()).append("</changefreq>\n");
//            xml.append("    <priority>").append(url.getPriority()).append("</priority>\n");
//            xml.append("  </url>\n");
//        }
//
//        xml.append("</urlset>");
//
//        return xml.toString();
//    }
//
//    /**
//     * Escapa caracteres especiais para XML
//     */
//    private String escapeXml(String text) {
//        if (text == null) return "";
//        return text.replace("&", "&amp;")
//                .replace("<", "&lt;")
//                .replace(">", "&gt;")
//                .replace("\"", "&quot;")
//                .replace("'", "&apos;");
//    }
//}
