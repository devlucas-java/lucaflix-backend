package com.lucaflix.service;

import com.lucaflix.dto.media.PaginatedResponseDTO;
import com.lucaflix.dto.media.SearchResultDTO;
import com.lucaflix.model.Movie;
import com.lucaflix.model.Serie;
import com.lucaflix.model.enums.Categoria;
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

    public PaginatedResponseDTO<SearchResultDTO> searchMedia(String texto, String categoria, String tipo, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<SearchResultDTO> allResults = new ArrayList<>();

        // Converter categoria string para enum se não for null
        Categoria categoriaEnum = null;
        if (categoria != null && !categoria.trim().isEmpty()) {
            try {
                categoriaEnum = Categoria.valueOf(categoria.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Categoria inválida, continua com null
            }
        }

        // Buscar filmes se tipo for "all" ou "movie"
        if ("all".equals(tipo) || "movie".equals(tipo)) {
            Page<Movie> movies = movieRepository.searchMovies(texto, categoriaEnum, pageable);
            List<SearchResultDTO> movieResults = movies.getContent().stream()
                    .map(this::convertMovieToSearchResult)
                    .collect(Collectors.toList());
            allResults.addAll(movieResults);
        }

        // Buscar séries se tipo for "all" ou "serie"
        if ("all".equals(tipo) || "serie".equals(tipo)) {
            Page<Serie> series = serieRepository.searchSeries(texto, categoriaEnum, pageable);
            List<SearchResultDTO> serieResults = series.getContent().stream()
                    .map(this::convertSerieToSearchResult)
                    .collect(Collectors.toList());
            allResults.addAll(serieResults);
        }

        // Calcular totais (simplificado - em produção você pode querer fazer isso de forma mais eficiente)
        long totalElements = allResults.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // Aplicar paginação manual aos resultados combinados
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, allResults.size());
        List<SearchResultDTO> paginatedResults = allResults.subList(startIndex, endIndex);

        PaginatedResponseDTO<SearchResultDTO> response = new PaginatedResponseDTO<>();
        response.setContent(paginatedResults);
        response.setCurrentPage(page);
        response.setTotalPages(totalPages);
        response.setTotalElements(totalElements);
        response.setSize(size);
        response.setFirst(page == 0);
        response.setLast(page >= totalPages - 1);

        return response;
    }

    private SearchResultDTO convertMovieToSearchResult(Movie movie) {
        SearchResultDTO dto = new SearchResultDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setType("movie");
        dto.setAnoLancamento(movie.getAnoLancamento());
        dto.setCategoria(movie.getCategoria());
        dto.setSinopse(movie.getSinopse());
        dto.setAvaliacao(movie.getAvaliacao());
        dto.setImageURL1(movie.getImageURL1());
        dto.setImageURL2(movie.getImageURL2());
        dto.setMinAge(movie.getMinAge());
        dto.setDuracaoMinutos(movie.getDuracaoMinutos());
        return dto;
    }

    private SearchResultDTO convertSerieToSearchResult(Serie serie) {
        SearchResultDTO dto = new SearchResultDTO();
        dto.setId(serie.getId());
        dto.setTitle(serie.getTitle());
        dto.setType("serie");
        dto.setAnoLancamento(serie.getAnoLancamento());
        dto.setCategoria(serie.getCategoria());
        dto.setSinopse(serie.getSinopse());
        dto.setAvaliacao(serie.getAvaliacao());
        dto.setImageURL1(serie.getImageURL1());
        dto.setImageURL2(serie.getImageURL2());
        dto.setMinAge(serie.getMinAge());
        dto.setTotalTemporadas(serie.getTotalTemporadas());
        dto.setTotalEpisodios(serie.getTotalEpisodios());
        return dto;
    }
}