package com.lucaflix.service;

import com.lucaflix.dto.SitemapUrlDto;
import com.lucaflix.model.Movie;
import com.lucaflix.model.Serie;
import com.lucaflix.model.enums.Categoria;
import com.lucaflix.repository.MovieRepository;
import com.lucaflix.repository.SerieRepository;
import com.lucaflix.service.utils.UrlFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SitemapService {

    private final MovieRepository movieRepository;
    private final SerieRepository serieRepository;
    private final UrlFormatter urlFormatter;

    @Value("${app.base-url:https://lucaflix.com}")
    private String baseUrl;

    /**
     * Gera todas as URLs para o sitemap com cache
     */
    @Cacheable(value = "sitemap", unless = "#result.isEmpty()")
    public List<SitemapUrlDto> generateSitemapUrls() {
        List<SitemapUrlDto> urls = new ArrayList<>();

        try {
            log.info("Iniciando geração do sitemap...");

            // URLs estáticas do site
            urls.addAll(getStaticUrls());
            log.debug("Adicionadas {} URLs estáticas", urls.size());

            // URLs dinâmicas dos filmes
            List<SitemapUrlDto> movieUrls = getMovieUrls();
            urls.addAll(movieUrls);
            log.debug("Adicionadas {} URLs de filmes", movieUrls.size());

            // URLs dinâmicas das séries
            List<SitemapUrlDto> serieUrls = getSerieUrls();
            urls.addAll(serieUrls);
            log.debug("Adicionadas {} URLs de séries", serieUrls.size());

            log.info("Sitemap gerado com sucesso: {} URLs totais", urls.size());

        } catch (Exception e) {
            log.error("Erro ao gerar URLs do sitemap: ", e);
        }

        return urls;
    }

    /**
     * URLs estáticas do site
     */
    private List<SitemapUrlDto> getStaticUrls() {
        List<SitemapUrlDto> staticUrls = new ArrayList<>();
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        // Página inicial
        staticUrls.add(createSitemapUrl("/", now, "daily", "1.0"));

        // Páginas principais
        staticUrls.add(createSitemapUrl("/filmes", now, "daily", "0.9"));
        staticUrls.add(createSitemapUrl("/series", now, "daily", "0.9"));
        staticUrls.add(createSitemapUrl("/categorias", now, "weekly", "0.8"));
        staticUrls.add(createSitemapUrl("/minha-lista", now, "daily", "0.7"));
        staticUrls.add(createSitemapUrl("/populares", now, "daily", "0.8"));
        staticUrls.add(createSitemapUrl("/mais-curtidos", now, "daily", "0.8"));

        // Páginas por categoria usando enum
        for (Categoria categoria : Categoria.values()) {
            if (categoria != Categoria.DESCONHECIDA) {
                String categoriaUrl = formatCategoriaForUrl(categoria);
                staticUrls.add(createSitemapUrl("/categoria/" + categoriaUrl, now, "weekly", "0.6"));
            }
        }

        // Páginas de anos (últimos 10 anos)
        int currentYear = LocalDateTime.now().getYear();
        for (int year = currentYear; year >= currentYear - 10; year--) {
            staticUrls.add(createSitemapUrl("/ano/" + year, now, "monthly", "0.5"));
        }

        return staticUrls;
    }

    /**
     * URLs dos filmes
     */
    private List<SitemapUrlDto> getMovieUrls() {
        List<SitemapUrlDto> movieUrls = new ArrayList<>();

        try {
            List<Movie> movies = movieRepository.findAllForSitemap();

            movieUrls = movies.stream()
                    .filter(movie -> movie.getTitle() != null && !movie.getTitle().trim().isEmpty())
                    .map(this::createMovieUrl)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Erro ao buscar filmes para sitemap: ", e);
        }

        return movieUrls;
    }

    /**
     * URLs das séries
     */
    private List<SitemapUrlDto> getSerieUrls() {
        List<SitemapUrlDto> serieUrls = new ArrayList<>();

        try {
            List<Serie> series = serieRepository.findAllForSitemap();

            serieUrls = series.stream()
                    .filter(serie -> serie.getTitle() != null && !serie.getTitle().trim().isEmpty())
                    .map(this::createSerieUrl)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Erro ao buscar séries para sitemap: ", e);
        }

        return serieUrls;
    }

    /**
     * Cria URL para filme individual
     */
    private SitemapUrlDto createMovieUrl(Movie movie) {
        String urlPath = urlFormatter.formatTitleForUrl(
                movie.getId(),
                movie.getTitle(),
                movie.getAnoLancamento()
        );

        String lastmod = formatLastModified(movie.getDataCadastro());

        return createSitemapUrl("/filme" + urlPath, lastmod, "monthly", "0.8");
    }

    /**
     * Cria URL para série individual
     */
    private SitemapUrlDto createSerieUrl(Serie serie) {
        String urlPath = urlFormatter.formatTitleForUrl(
                serie.getId(),
                serie.getTitle(),
                serie.getAnoLancamento()
        );

        String lastmod = formatLastModified(serie.getDataCadastro());

        return createSitemapUrl("/serie" + urlPath, lastmod, "monthly", "0.8");
    }

    /**
     * Helper para criar SitemapUrlDto
     */
    private SitemapUrlDto createSitemapUrl(String path, String lastmod, String changefreq, String priority) {
        return new SitemapUrlDto(
                baseUrl + path,
                lastmod,
                changefreq,
                priority
        );
    }

    /**
     * Formata data para lastmod
     */
    private String formatLastModified(java.util.Date date) {
        if (date != null) {
            return date.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * Formata categoria para URL
     */
    private String formatCategoriaForUrl(Categoria categoria) {
        return categoria.name()
                .toLowerCase()
                .replace("_", "-")
                .replace("ficcao-cientifica", "sci-fi");
    }

    /**
     * Gera o XML do sitemap
     */
    public String generateSitemapXml() {
        List<SitemapUrlDto> urls = generateSitemapUrls();

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"");
        xml.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        xml.append(" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9");
        xml.append(" http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">\n");

        for (SitemapUrlDto url : urls) {
            xml.append("  <url>\n");
            xml.append("    <loc>").append(escapeXml(url.getLoc())).append("</loc>\n");

            if (url.getLastmod() != null) {
                xml.append("    <lastmod>").append(url.getLastmod()).append("</lastmod>\n");
            }

            if (url.getChangefreq() != null) {
                xml.append("    <changefreq>").append(url.getChangefreq()).append("</changefreq>\n");
            }

            if (url.getPriority() != null) {
                xml.append("    <priority>").append(url.getPriority()).append("</priority>\n");
            }

            xml.append("  </url>\n");
        }

        xml.append("</urlset>");

        return xml.toString();
    }

    /**
     * Gera sitemap index para múltiplos sitemaps (caso necessário)
     */
    public String generateSitemapIndex() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        // Sitemap principal
        xml.append("  <sitemap>\n");
        xml.append("    <loc>").append(baseUrl).append("/api/sitemap.xml</loc>\n");
        xml.append("    <lastmod>").append(now).append("</lastmod>\n");
        xml.append("  </sitemap>\n");

        xml.append("</sitemapindex>");

        return xml.toString();
    }

    /**
     * Valida URLs do sitemap
     */
    public boolean validateSitemapUrls() {
        try {
            List<SitemapUrlDto> urls = generateSitemapUrls();

            // Verifica se há URLs
            if (urls.isEmpty()) {
                log.warn("Sitemap vazio");
                return false;
            }

            // Verifica limite de URLs (50.000 por sitemap)
            if (urls.size() > 50000) {
                log.warn("Sitemap excede limite de 50.000 URLs: {}", urls.size());
                return false;
            }

            // Verifica URLs duplicadas
            long uniqueUrls = urls.stream()
                    .map(SitemapUrlDto::getLoc)
                    .distinct()
                    .count();

            if (uniqueUrls != urls.size()) {
                log.warn("Encontradas URLs duplicadas no sitemap");
                return false;
            }

            log.info("Sitemap validado com sucesso: {} URLs únicas", uniqueUrls);
            return true;

        } catch (Exception e) {
            log.error("Erro ao validar sitemap: ", e);
            return false;
        }
    }

    /**
     * Escapa caracteres especiais para XML
     */
    private String escapeXml(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    /**
     * Limpa cache do sitemap
     */
    @org.springframework.cache.annotation.CacheEvict(value = "sitemap", allEntries = true)
    public void clearSitemapCache() {
        log.info("Cache do sitemap limpo");
    }
}