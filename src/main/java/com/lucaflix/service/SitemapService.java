package com.lucaflix.service;

import com.lucaflix.dto.SitemapUrlDto;
import com.lucaflix.model.Anime;
import com.lucaflix.model.Movie;
import com.lucaflix.model.Serie;
import com.lucaflix.model.enums.Categoria;
import com.lucaflix.repository.AnimeRepository;
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
    private final AnimeRepository animeRepository;
    private final UrlFormatter urlFormatter;

    // Usando o domínio correto
    private final String baseUrl = "https://lucaflix.com";

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

            // URLs dinâmicas dos animes
            List<SitemapUrlDto> animeUrls = getAnimeUrls();
            urls.addAll(animeUrls);
            log.debug("Adicionadas {} URLs de animes", animeUrls.size());

            log.info("Sitemap gerado com sucesso: {} URLs totais", urls.size());

        } catch (Exception e) {
            log.error("Erro ao gerar URLs do sitemap: ", e);
        }

        return urls;
    }

    /**
     * URLs estáticas do site - usando padrões do frontend
     */
    private List<SitemapUrlDto> getStaticUrls() {
        List<SitemapUrlDto> staticUrls = new ArrayList<>();
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        // Página inicial
        staticUrls.add(createSitemapUrl("/", now, "daily", "1.0"));

        // Páginas principais (URLs em português)
        staticUrls.add(createSitemapUrl("/filmes", now, "daily", "0.9"));
        staticUrls.add(createSitemapUrl("/series", now, "daily", "0.9"));
        staticUrls.add(createSitemapUrl("/animes", now, "daily", "0.9"));

        // Páginas protegidas
        staticUrls.add(createSitemapUrl("/minha-lista", now, "daily", "0.7"));
        staticUrls.add(createSitemapUrl("/busca", now, "weekly", "0.8"));

        // Páginas de busca por categoria e tipo
        String[] tipos = {"filme", "serie", "anime"};
        for (Categoria categoria : Categoria.values()) {
            if (categoria != Categoria.DESCONHECIDA) {
                String categoriaUrl = formatCategoriaForUrl(categoria);

                // Páginas de categoria geral (sem tipo específico)
                staticUrls.add(createSitemapUrl("/busca/" + categoriaUrl, now, "weekly", "0.6"));

                // Páginas de categoria por tipo
                for (String tipo : tipos) {
                    staticUrls.add(createSitemapUrl("/busca/" + categoriaUrl + "/" + tipo, now, "weekly", "0.6"));
                }
            }
        }

        // Páginas de busca por tipo
        staticUrls.add(createSitemapUrl("/busca/filme", now, "weekly", "0.7"));
        staticUrls.add(createSitemapUrl("/busca/serie", now, "weekly", "0.7"));
        staticUrls.add(createSitemapUrl("/busca/anime", now, "weekly", "0.7"));

        // Páginas institucionais
        staticUrls.add(createSitemapUrl("/termos-de-uso", now, "yearly", "0.3"));
        staticUrls.add(createSitemapUrl("/politica-de-privacidade", now, "yearly", "0.3"));

        return staticUrls;
    }

    /**
     * URLs dos filmes - seguindo padrão do frontend
     */
    private List<SitemapUrlDto> getMovieUrls() {
        List<SitemapUrlDto> movieUrls = new ArrayList<>();

        try {
            List<Movie> movies = movieRepository.findAllForSitemap();

            for (Movie movie : movies) {
                if (movie.getTitle() != null && !movie.getTitle().trim().isEmpty()) {
                    // Gerar múltiplas URLs conforme o frontend
                    movieUrls.addAll(createMovieUrls(movie));
                }
            }

        } catch (Exception e) {
            log.error("Erro ao buscar filmes para sitemap: ", e);
        }

        return movieUrls;
    }

    /**
     * URLs das séries - seguindo padrão do frontend
     */
    private List<SitemapUrlDto> getSerieUrls() {
        List<SitemapUrlDto> serieUrls = new ArrayList<>();

        try {
            List<Serie> series = serieRepository.findAllForSitemap();

            for (Serie serie : series) {
                if (serie.getTitle() != null && !serie.getTitle().trim().isEmpty()) {
                    // Gerar múltiplas URLs conforme o frontend
                    serieUrls.addAll(createSerieUrls(serie));
                }
            }

        } catch (Exception e) {
            log.error("Erro ao buscar séries para sitemap: ", e);
        }

        return serieUrls;
    }

    /**
     * URLs dos animes - seguindo padrão do frontend
     */
    private List<SitemapUrlDto> getAnimeUrls() {
        List<SitemapUrlDto> animeUrls = new ArrayList<>();

        try {
            List<Anime> animes = animeRepository.findAllForSitemap();

            for (Anime anime : animes) {
                if (anime.getTitle() != null && !anime.getTitle().trim().isEmpty()) {
                    // Gerar múltiplas URLs conforme o frontend
                    animeUrls.addAll(createAnimeUrls(anime));
                }
            }

        } catch (Exception e) {
            log.error("Erro ao buscar animes para sitemap: ", e);
        }

        return animeUrls;
    }

    /**
     * Cria todas as URLs possíveis para um filme conforme o frontend
     */
    private List<SitemapUrlDto> createMovieUrls(Movie movie) {
        List<SitemapUrlDto> urls = new ArrayList<>();
        String titleSlug = urlFormatter.formatTitleForUrl(
                movie.getId(),
                movie.getTitle(),
                movie.getAnoLancamento()
        );
        String lastmod = formatLastModified(movie.getDataCadastro());

        // URLs principais do filme (as mais importantes para SEO)
        urls.add(createSitemapUrl("/filme/" + movie.getId() + "/" + extractSlugFromPath(titleSlug), lastmod, "monthly", "0.9"));
        urls.add(createSitemapUrl("/filmes/filme/" + movie.getId() + "/" + extractSlugFromPath(titleSlug), lastmod, "monthly", "0.8"));

        // URLs sem slug (para compatibilidade)
        urls.add(createSitemapUrl("/filme/" + movie.getId(), lastmod, "monthly", "0.7"));
        urls.add(createSitemapUrl("/filmes/filme/" + movie.getId(), lastmod, "monthly", "0.6"));

        return urls;
    }

    /**
     * Cria todas as URLs possíveis para uma série conforme o frontend
     */
    private List<SitemapUrlDto> createSerieUrls(Serie serie) {
        List<SitemapUrlDto> urls = new ArrayList<>();
        String titleSlug = urlFormatter.formatTitleForUrl(
                serie.getId(),
                serie.getTitle(),
                serie.getAnoLancamento()
        );
        String lastmod = formatLastModified(serie.getDataCadastro());

        // URLs principais da série (as mais importantes para SEO)
        urls.add(createSitemapUrl("/serie/" + serie.getId() + "/" + extractSlugFromPath(titleSlug), lastmod, "monthly", "0.9"));
        urls.add(createSitemapUrl("/series/serie/" + serie.getId() + "/" + extractSlugFromPath(titleSlug), lastmod, "monthly", "0.8"));

        // URLs sem slug (para compatibilidade)
        urls.add(createSitemapUrl("/serie/" + serie.getId(), lastmod, "monthly", "0.7"));
        urls.add(createSitemapUrl("/series/serie/" + serie.getId(), lastmod, "monthly", "0.6"));

        return urls;
    }

    /**
     * Cria todas as URLs possíveis para um anime conforme o frontend
     */
    private List<SitemapUrlDto> createAnimeUrls(Anime anime) {
        List<SitemapUrlDto> urls = new ArrayList<>();
        String titleSlug = urlFormatter.formatTitleForUrl(
                anime.getId(),
                anime.getTitle(),
                anime.getAnoLancamento()
        );
        String lastmod = formatLastModified(anime.getDataCadastro());

        // URLs principais do anime (as mais importantes para SEO)
        urls.add(createSitemapUrl("/anime/" + anime.getId() + "/" + extractSlugFromPath(titleSlug), lastmod, "monthly", "0.9"));
        urls.add(createSitemapUrl("/animes/anime/" + anime.getId() + "/" + extractSlugFromPath(titleSlug), lastmod, "monthly", "0.8"));

        // URLs sem slug (para compatibilidade)
        urls.add(createSitemapUrl("/anime/" + anime.getId(), lastmod, "monthly", "0.7"));
        urls.add(createSitemapUrl("/animes/anime/" + anime.getId(), lastmod, "monthly", "0.6"));

        return urls;
    }

    /**
     * Extrai o slug do path retornado pelo UrlFormatter
     */
    private String extractSlugFromPath(String fullPath) {
        // Se o UrlFormatter retorna "/123/titulo-do-filme-2023", extrair apenas "titulo-do-filme-2023"
        if (fullPath != null && fullPath.contains("/")) {
            String[] parts = fullPath.split("/");
            if (parts.length >= 3) {
                return parts[2]; // Retorna a parte do slug
            }
        }
        return fullPath != null ? fullPath.replaceFirst("^/\\d+/", "") : "";
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
     * Gera sitemap index para múltiplos sitemaps
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

        // Sitemaps separados por tipo
        xml.append("  <sitemap>\n");
        xml.append("    <loc>").append(baseUrl).append("/api/sitemap-filmes.xml</loc>\n");
        xml.append("    <lastmod>").append(now).append("</lastmod>\n");
        xml.append("  </sitemap>\n");

        xml.append("  <sitemap>\n");
        xml.append("    <loc>").append(baseUrl).append("/api/sitemap-series.xml</loc>\n");
        xml.append("    <lastmod>").append(now).append("</lastmod>\n");
        xml.append("  </sitemap>\n");

        xml.append("  <sitemap>\n");
        xml.append("    <loc>").append(baseUrl).append("/api/sitemap-animes.xml</loc>\n");
        xml.append("    <lastmod>").append(now).append("</lastmod>\n");
        xml.append("  </sitemap>\n");

        xml.append("</sitemapindex>");

        return xml.toString();
    }

    /**
     * Gera sitemap específico para filmes
     */
    public String generateMoviesSitemapXml() {
        List<SitemapUrlDto> movieUrls = getMovieUrls();
        return generateXmlFromUrls(movieUrls);
    }

    /**
     * Gera sitemap específico para séries
     */
    public String generateSeriesSitemapXml() {
        List<SitemapUrlDto> serieUrls = getSerieUrls();
        return generateXmlFromUrls(serieUrls);
    }

    /**
     * Gera sitemap específico para animes
     */
    public String generateAnimesSitemapXml() {
        List<SitemapUrlDto> animeUrls = getAnimeUrls();
        return generateXmlFromUrls(animeUrls);
    }

    /**
     * Helper para gerar XML a partir de uma lista de URLs
     */
    private String generateXmlFromUrls(List<SitemapUrlDto> urls) {
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

    /**
     * Estatísticas do sitemap
     */
    public SitemapStats getSitemapStats() {
        try {
            List<SitemapUrlDto> urls = generateSitemapUrls();

            long movieCount = urls.stream()
                    .filter(url -> url.getLoc().contains("/filme/"))
                    .count();

            long serieCount = urls.stream()
                    .filter(url -> url.getLoc().contains("/serie/"))
                    .count();

            long animeCount = urls.stream()
                    .filter(url -> url.getLoc().contains("/anime/"))
                    .count();

            long staticCount = urls.size() - movieCount - serieCount - animeCount;

            return new SitemapStats(urls.size(), movieCount, serieCount, animeCount, staticCount);

        } catch (Exception e) {
            log.error("Erro ao gerar estatísticas do sitemap: ", e);
            return new SitemapStats(0, 0, 0, 0, 0);
        }
    }

    /**
     * Classe para estatísticas do sitemap
     */
    public static class SitemapStats {
        private final long totalUrls;
        private final long movieUrls;
        private final long serieUrls;
        private final long animeUrls;
        private final long staticUrls;

        public SitemapStats(long totalUrls, long movieUrls, long serieUrls, long animeUrls, long staticUrls) {
            this.totalUrls = totalUrls;
            this.movieUrls = movieUrls;
            this.serieUrls = serieUrls;
            this.animeUrls = animeUrls;
            this.staticUrls = staticUrls;
        }

        // Getters
        public long getTotalUrls() { return totalUrls; }
        public long getMovieUrls() { return movieUrls; }
        public long getSerieUrls() { return serieUrls; }
        public long getAnimeUrls() { return animeUrls; }
        public long getStaticUrls() { return staticUrls; }
    }
}