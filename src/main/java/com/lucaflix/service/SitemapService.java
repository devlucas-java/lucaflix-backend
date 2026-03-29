package com.lucaflix.service;

import com.lucaflix.dto.response.others.SitemapUrlDto;
import com.lucaflix.repository.AnimeRepository;
import com.lucaflix.repository.MovieRepository;
import com.lucaflix.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SitemapService {

    private final MovieRepository movieRepository;
    private final SeriesRepository seriesRepository;
    private final AnimeRepository animeRepository;

    private static final String BASE_URL = "https://lucaflix.com";

    @Cacheable("sitemap")
    public String generateSitemapXml() {
        List<SitemapUrlDto> urls = new ArrayList<>();

        addStaticUrls(urls);

        movieRepository.findAll()
                .forEach(m -> urls.add(buildUrl("/movie/", m.getTitle(), m.getId())));

        seriesRepository.findAll()
                .forEach(s -> urls.add(buildUrl("/serie/", s.getTitle(), s.getId())));

        animeRepository.findAll()
                .forEach(a -> urls.add(buildUrl("/anime/", a.getTitle(), a.getId())));

        log.info("Total URLs: {}", urls.size());

        return buildXml(urls);
    }

    private void addStaticUrls(List<SitemapUrlDto> urls) {
        String now = LocalDate.now().toString();

        urls.add(new SitemapUrlDto(BASE_URL + "/", now, "daily", "1.0"));
        urls.add(new SitemapUrlDto(BASE_URL + "/movies", now, "daily", "0.9"));
        urls.add(new SitemapUrlDto(BASE_URL + "/series", now, "daily", "0.9"));
        urls.add(new SitemapUrlDto(BASE_URL + "/animes", now, "daily", "0.9"));
    }

    private SitemapUrlDto buildUrl(String path, String title, UUID id) {
        return new SitemapUrlDto(
                BASE_URL + path + id + "/" + slug(title),
                LocalDate.now().toString(),
                "weekly",
                "0.8"
        );
    }

    private String buildXml(List<SitemapUrlDto> urls) {
        StringBuilder xml = new StringBuilder();

        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        for (SitemapUrlDto url : urls) {
            xml.append("  <url>\n");
            xml.append("    <loc>").append(url.getLoc()).append("</loc>\n");
            xml.append("    <lastmod>").append(url.getLastmod()).append("</lastmod>\n");
            xml.append("    <changefreq>").append(url.getChangefreq()).append("</changefreq>\n");
            xml.append("    <priority>").append(url.getPriority()).append("</priority>\n");
            xml.append("  </url>\n");
        }

        xml.append("</urlset>");
        return xml.toString();
    }

    private String slug(String title) {
        return  title.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");

    }
}