package com.lucaflix.service;

import com.lucaflix.dto.response.movie.MovieSimpleDTO;
import com.lucaflix.dto.response.anime.AnimeSimpleDTO;
import com.lucaflix.dto.response.page.PaginatedResponseDTO;
import com.lucaflix.dto.response.serie.SerieSimpleDTO;
import com.lucaflix.model.Anime;
import com.lucaflix.model.Movie;
import com.lucaflix.model.Series;
import com.lucaflix.model.enums.Categories;
import com.lucaflix.repository.AnimeRepository;
import com.lucaflix.repository.MovieRepository;
import com.lucaflix.repository.SerieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final MovieRepository movieRepository;
    private final SerieRepository serieRepository;
    private final AnimeRepository animeRepository;

    public PaginatedResponseDTO<Object> searchMedia(String texto, String categoria, String tipo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Object> allResults = new ArrayList<>();

        // Converter categoria string para enum se não for null
        Categories categoriesEnum = null;
        if (categoria != null && !categoria.trim().isEmpty()) {
            try {
                categoriesEnum = Categories.valueOf(categoria.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Categoria inválida, continua com null
            }
        }

        long totalElements = 0;

        // Buscar filmes se tipo for "all" ou "movie"
        if ("all".equals(tipo) || "movie".equals(tipo)) {
            Page<Movie> movies = movieRepository.searchMovies(texto, categoriesEnum, pageable);
            List<MovieSimpleDTO> movieResults = movies.getContent().stream()
                    .map(this::convertMovieToSimpleDTO)
                    .collect(Collectors.toList());
            allResults.addAll(movieResults);

            if ("movie".equals(tipo)) {
                totalElements = movies.getTotalElements();
            }
        }

        // Buscar séries se tipo for "all" ou "serie"
        if ("all".equals(tipo) || "serie".equals(tipo)) {
            Page<Series> series = serieRepository.searchSeries(texto, categoriesEnum, pageable);
            List<SerieSimpleDTO> serieResults = series.getContent().stream()
                    .map(this::convertSerieToSimpleDTO)
                    .collect(Collectors.toList());
            allResults.addAll(serieResults);

            if ("serie".equals(tipo)) {
                totalElements = series.getTotalElements();
            }
        }

        // Buscar animes se tipo for "all" ou "anime"
        if ("all".equals(tipo) || "anime".equals(tipo)) {
            Page<Anime> animes = animeRepository.searchAnimes(texto, categoriesEnum, pageable);
            List<AnimeSimpleDTO> animeResults = animes.getContent().stream()
                    .map(this::convertAnimeToSimpleDTO)
                    .collect(Collectors.toList());
            allResults.addAll(animeResults);

            if ("anime".equals(tipo)) {
                totalElements = animes.getTotalElements();
            }
        }

        // Para busca "all", calcular total combinado (aproximado)
        if ("all".equals(tipo)) {
            // Para busca combinada, fazemos uma estimativa baseada nos resultados
            totalElements = allResults.size();

            // Se temos resultados completos na página, assumimos que há mais páginas
            if (allResults.size() == size) {
                // Faz contagem real para ser mais preciso
                long movieCount = movieRepository.searchMovies(texto, categoriesEnum, PageRequest.of(0, 1)).getTotalElements();
                long serieCount = serieRepository.searchSeries(texto, categoriesEnum, PageRequest.of(0, 1)).getTotalElements();
                long animeCount = animeRepository.searchAnimes(texto, categoriesEnum, PageRequest.of(0, 1)).getTotalElements();
                totalElements = movieCount + serieCount + animeCount;
            }
        }

        // Calcular total de páginas
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // Para tipo "all", aplicar paginação manual se necessário
        if ("all".equals(tipo) && allResults.size() > size) {
            int startIndex = 0;
            int endIndex = Math.min(size, allResults.size());
            allResults = allResults.subList(startIndex, endIndex);
        }

        // Criar resposta paginada
        PaginatedResponseDTO<Object> response = new PaginatedResponseDTO<>();
        response.setContent(allResults);
        response.setCurrentPage(page);
        response.setTotalPages(totalPages);
        response.setTotalElements(totalElements);
        response.setSize(size);
        response.setFirst(page == 0);
        response.setLast(page >= totalPages - 1 || totalPages == 0);

        return response;
    }

    private MovieSimpleDTO convertMovieToSimpleDTO(Movie movie) {
        MovieSimpleDTO dto = new MovieSimpleDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setAnoLancamento(movie.getAnoLancamento());
        dto.setDuracaoMinutos(movie.getDuracaoMinutos());
        dto.setTmdbId(movie.getTmdbId());
        dto.setImdbId(movie.getImdbId());
        dto.setPaisOrigen(movie.getPaisOrigen());
        dto.setCategories(movie.getCategories());
        dto.setMinAge(movie.getMinAge());
        dto.setAvaliacao(movie.getAvaliacao());
        dto.setPosterURL2(movie.getPosterURL2());
        dto.setPosterURL1(movie.getPosterURL1());

        // Calcular total de likes
        dto.setTotalLikes(movie.getLikes() != null ? (long) movie.getLikes().size() : 0L);

        return dto;
    }

    private SerieSimpleDTO convertSerieToSimpleDTO(Series series) {
        SerieSimpleDTO dto = new SerieSimpleDTO();
        dto.setId(series.getId());
        dto.setTitle(series.getTitle());
        dto.setAnoLancamento(series.getAnoLancamento());
        dto.setTmdbId(series.getTmdbId());
        dto.setImdbId(series.getImdbId());
        dto.setPaisOrigen(series.getPaisOrigem());
        dto.setCategories(series.getCategoria());
        dto.setMinAge(series.getMinAge());
        dto.setAvaliacao(series.getAvaliacao());
        dto.setPosterURL1(series.getPosterURL1());
        dto.setPosterURL2(series.getPosterURL2());
        dto.setTotalTemporadas(series.getTotalTemporadas());
        dto.setTotalEpisodios(series.getTotalEpisodios());

        // Calcular total de likes
        dto.setTotalLikes(series.getLikes() != null ? (long) series.getLikes().size() : 0L);

        return dto;
    }

    private AnimeSimpleDTO convertAnimeToSimpleDTO(Anime anime) {
        AnimeSimpleDTO dto = new AnimeSimpleDTO();
        dto.setId(anime.getId());
        dto.setTitle(anime.getTitle());
        dto.setAnoLancamento(anime.getAnoLancamento());
        dto.setTmdbId(anime.getTmdbId());
        dto.setImdbId(anime.getImdbId());
        dto.setPaisOrigen(anime.getPaisOrigen());
        dto.setCategories(anime.getCategories());
        dto.setMinAge(anime.getMinAge());
        dto.setAvaliacao(anime.getAvaliacao());
        dto.setEmbed1(anime.getEmbed1());
        dto.setEmbed2(anime.getEmbed2());
        dto.setTrailer(anime.getTrailer());
        dto.setPosterURL1(anime.getPosterURL1());
        dto.setPosterURL2(anime.getPosterURL2());
        dto.setTotalTemporadas(anime.getTotalTemporadas());
        dto.setTotalEpisodios(anime.getTotalEpisodios());

        // Calcular total de likes
        dto.setTotalLikes(anime.getLikes() != null ? (long) anime.getLikes().size() : 0L);

        return dto;
    }
}