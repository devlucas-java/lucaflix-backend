package com.lucaflix.service;

import com.lucaflix.model.Anime;
import com.lucaflix.model.Movie;
import com.lucaflix.model.Series;
import com.lucaflix.repository.AnimeRepository;
import com.lucaflix.repository.MovieRepository;
import com.lucaflix.repository.SeriesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SitemapServiceTest {

    @InjectMocks
    private SitemapService sitemapService;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private AnimeRepository animeRepository;

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Movie buildMovie(String title) {
        Movie movie = new Movie();
        movie.setId(UUID.randomUUID());
        movie.setTitle(title);
        return movie;
    }

    private Series buildSeries(String title) {
        Series series = new Series();
        series.setId(UUID.randomUUID());
        series.setTitle(title);
        return series;
    }

    private Anime buildAnime(String title) {
        Anime anime = new Anime();
        anime.setId(UUID.randomUUID());
        anime.setTitle(title);
        return anime;
    }

    // -------------------------------------------------------------------------
    // generateSitemapXml — all structure
    // -------------------------------------------------------------------------

    @Test
    void generateSitemapXml_ShouldReturnValidXmlStructure() {
        when(movieRepository.findAll()).thenReturn(List.of());
        when(seriesRepository.findAll()).thenReturn(List.of());
        when(animeRepository.findAll()).thenReturn(List.of());

        String xml = sitemapService.generateSitemapXml();

        assertThat(xml).startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        assertThat(xml).contains("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
        assertThat(xml).endsWith("</urlset>");
    }

    @Test
    void generateSitemapXml_ShouldContainStaticUrls() {
        when(movieRepository.findAll()).thenReturn(List.of());
        when(seriesRepository.findAll()).thenReturn(List.of());
        when(animeRepository.findAll()).thenReturn(List.of());

        String xml = sitemapService.generateSitemapXml();

        assertThat(xml).contains("https://lucaflix.com/");
        assertThat(xml).contains("https://lucaflix.com/movies");
        assertThat(xml).contains("https://lucaflix.com/series");
        assertThat(xml).contains("https://lucaflix.com/animes");
    }

    @Test
    void generateSitemapXml_ShouldContainCorrectChangefreqForStaticUrls() {
        when(movieRepository.findAll()).thenReturn(List.of());
        when(seriesRepository.findAll()).thenReturn(List.of());
        when(animeRepository.findAll()).thenReturn(List.of());

        String xml = sitemapService.generateSitemapXml();

        // 4 URLs estáticas com changefreq daily
        long dailyCount = xml.lines()
                .filter(line -> line.contains("<changefreq>daily</changefreq>"))
                .count();

        assertThat(dailyCount).isEqualTo(4);
    }

    // -------------------------------------------------------------------------
    // generateSitemapXml — movies
    // -------------------------------------------------------------------------

    @Test
    void generateSitemapXml_ShouldIncludeMovieUrls() {
        Movie movie = buildMovie("Inception");
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        when(seriesRepository.findAll()).thenReturn(List.of());
        when(animeRepository.findAll()).thenReturn(List.of());

        String xml = sitemapService.generateSitemapXml();

        assertThat(xml).contains("/movie/" + movie.getId() + "/inception");
    }

    @Test
    void generateSitemapXml_ShouldIncludeMultipleMovies() {
        Movie m1 = buildMovie("Avatar");
        Movie m2 = buildMovie("Interstellar");
        when(movieRepository.findAll()).thenReturn(List.of(m1, m2));
        when(seriesRepository.findAll()).thenReturn(List.of());
        when(animeRepository.findAll()).thenReturn(List.of());

        String xml = sitemapService.generateSitemapXml();

        assertThat(xml).contains("/movie/" + m1.getId() + "/avatar");
        assertThat(xml).contains("/movie/" + m2.getId() + "/interstellar");
    }

    // -------------------------------------------------------------------------
    // generateSitemapXml — séries
    // -------------------------------------------------------------------------

    @Test
    void generateSitemapXml_ShouldIncludeSeriesUrls() {
        Series series = buildSeries("Breaking Bad");
        when(movieRepository.findAll()).thenReturn(List.of());
        when(seriesRepository.findAll()).thenReturn(List.of(series));
        when(animeRepository.findAll()).thenReturn(List.of());

        String xml = sitemapService.generateSitemapXml();

        assertThat(xml).contains("/serie/" + series.getId() + "/breaking-bad");
    }

    // -------------------------------------------------------------------------
    // generateSitemapXml — animes
    // -------------------------------------------------------------------------

    @Test
    void generateSitemapXml_ShouldIncludeAnimeUrls() {
        Anime anime = buildAnime("Attack on Titan");
        when(movieRepository.findAll()).thenReturn(List.of());
        when(seriesRepository.findAll()).thenReturn(List.of());
        when(animeRepository.findAll()).thenReturn(List.of(anime));

        String xml = sitemapService.generateSitemapXml();

        assertThat(xml).contains("/anime/" + anime.getId() + "/attack-on-titan");
    }

    // -------------------------------------------------------------------------
    // generateSitemapXml — count total of URLs
    // -------------------------------------------------------------------------

    @Test
    void generateSitemapXml_ShouldContainCorrectTotalUrlCount() {
        when(movieRepository.findAll()).thenReturn(List.of(buildMovie("Film A"), buildMovie("Film B")));
        when(seriesRepository.findAll()).thenReturn(List.of(buildSeries("Serie A")));
        when(animeRepository.findAll()).thenReturn(List.of(buildAnime("Anime A"), buildAnime("Anime B")));

        String xml = sitemapService.generateSitemapXml();

        long urlCount = xml.lines()
                .filter(line -> line.trim().equals("<url>"))
                .count();

        assertThat(urlCount).isEqualTo(9);
    }

    @Test
    void generateSitemapXml_ShouldContainOnlyStaticUrls_WhenRepositoriesAreEmpty() {
        when(movieRepository.findAll()).thenReturn(List.of());
        when(seriesRepository.findAll()).thenReturn(List.of());
        when(animeRepository.findAll()).thenReturn(List.of());

        String xml = sitemapService.generateSitemapXml();

        long urlCount = xml.lines()
                .filter(line -> line.trim().equals("<url>"))
                .count();

        assertThat(urlCount).isEqualTo(4);
    }

    // -------------------------------------------------------------------------
    // slug — special characters
    // -------------------------------------------------------------------------

    @Test
    void generateSitemapXml_ShouldSlugifyTitleWithSpecialCharacters() {
        Movie movie = buildMovie("Spider-Man: No Way Home!");
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        when(seriesRepository.findAll()).thenReturn(List.of());
        when(animeRepository.findAll()).thenReturn(List.of());

        String xml = sitemapService.generateSitemapXml();

        // Caracteres especiais e hífens removidos, espaços viram "-"
        assertThat(xml).contains("/movie/" + movie.getId() + "/spiderman-no-way-home");
    }

    @Test
    void generateSitemapXml_ShouldSlugifyTitleWithUppercase() {
        Movie movie = buildMovie("THE DARK KNIGHT");
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        when(seriesRepository.findAll()).thenReturn(List.of());
        when(animeRepository.findAll()).thenReturn(List.of());

        String xml = sitemapService.generateSitemapXml();

        assertThat(xml).contains("/movie/" + movie.getId() + "/the-dark-knight");
    }

    @Test
    void generateSitemapXml_ShouldSlugifyTitleWithMultipleSpaces() {
        Movie movie = buildMovie("Fast   and   Furious");
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        when(seriesRepository.findAll()).thenReturn(List.of());
        when(animeRepository.findAll()).thenReturn(List.of());

        String xml = sitemapService.generateSitemapXml();

        assertThat(xml).contains("/movie/" + movie.getId() + "/fast-and-furious");
    }

    // -------------------------------------------------------------------------
    // generateSitemapXml — priority and changefreq
    // -------------------------------------------------------------------------

    @Test
    void generateSitemapXml_ShouldHaveWeeklyChangefreqForDynamicUrls() {
        when(movieRepository.findAll()).thenReturn(List.of(buildMovie("Movie X")));
        when(seriesRepository.findAll()).thenReturn(List.of());
        when(animeRepository.findAll()).thenReturn(List.of());

        String xml = sitemapService.generateSitemapXml();

        assertThat(xml).contains("<changefreq>weekly</changefreq>");
    }

    @Test
    void generateSitemapXml_ShouldHavePriority08ForDynamicUrls() {
        when(movieRepository.findAll()).thenReturn(List.of(buildMovie("Movie X")));
        when(seriesRepository.findAll()).thenReturn(List.of());
        when(animeRepository.findAll()).thenReturn(List.of());

        String xml = sitemapService.generateSitemapXml();

        assertThat(xml).contains("<priority>0.8</priority>");
    }

    // -------------------------------------------------------------------------
    // Repository calls
    // -------------------------------------------------------------------------

    @Test
    void generateSitemapXml_ShouldCallAllRepositories() {
        when(movieRepository.findAll()).thenReturn(List.of());
        when(seriesRepository.findAll()).thenReturn(List.of());
        when(animeRepository.findAll()).thenReturn(List.of());

        sitemapService.generateSitemapXml();

        verify(movieRepository, times(1)).findAll();
        verify(seriesRepository, times(1)).findAll();
        verify(animeRepository, times(1)).findAll();
    }
}