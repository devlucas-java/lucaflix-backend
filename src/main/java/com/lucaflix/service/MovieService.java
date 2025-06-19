package com.lucaflix.service;

import com.lucaflix.dto.media.movie.MovieCompleteDTO;
import com.lucaflix.dto.media.movie.MovieFilter;
import com.lucaflix.dto.media.movie.MovieMapper;
import com.lucaflix.dto.media.movie.MovieSimpleDTO;
import com.lucaflix.dto.media.PaginatedResponseDTO;
import com.lucaflix.model.*;
import com.lucaflix.model.enums.Categoria;
import com.lucaflix.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final LikeRepository likeRepository;
    private final MinhaListaRepository minhaListaRepository;
    private final UserRepository userRepository;
    private final MovieMapper movieMapper;

    // Filtro de mídias
    public PaginatedResponseDTO<MovieSimpleDTO> filtrarMedia(MovieFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Movie> mediaPage = movieRepository.buscarPorFiltros(
                filter.getTitle(),
                filter.getAvaliacao(),
                filter.getCategoria(),
                pageable
        );
        return movieMapper.createPaginatedResponse(mediaPage);
    }

    // Top 10 mais curtidas
    public List<MovieSimpleDTO> getTop10MostLiked() {
        List<Movie> topMovies = movieRepository.findTop10ByLikes(PageRequest.of(0, 10));
        return topMovies.stream()
                .map(movieMapper::convertToSimpleDTO)
                .collect(Collectors.toList());
    }

    public MovieCompleteDTO getMediaById(Long mediaId, UUID userId) {
        Movie movie = movieRepository.findById(mediaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Filme não encontrado"
                ));
        return movieMapper.convertToCompleteDTO(movie, userId);
    }

    // Toggle Like - adiciona se não existir, remove se existir
    @Transactional
    public boolean toggleLike(UUID userId, Long mediaId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Movie movie = movieRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Mídia não encontrada"));

        Like existingLike = likeRepository.findByUserAndMovie(user, movie).orElse(null);

        if (existingLike != null) {
            likeRepository.delete(existingLike);
            return false; // Removeu like
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setMovie(movie);
            likeRepository.save(like);
            return true; // Adicionou like
        }
    }

    // Toggle Minha Lista - adiciona se não existir, remove se existir
    @Transactional
    public boolean toggleMyList(UUID userId, Long mediaId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Movie movie = movieRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Mídia não encontrada"));

        MinhaLista existingItem = minhaListaRepository.findByUserAndMovie(user, movie).orElse(null);

        if (existingItem != null) {
            minhaListaRepository.delete(existingItem);
            return false; // Removeu da lista
        } else {
            MinhaLista minhaLista = new MinhaLista();
            minhaLista.setUser(user);
            minhaLista.setMovie(movie);
            minhaListaRepository.save(minhaLista);
            return true; // Adicionou à lista
        }
    }

    // Mídias populares (mais curtidas)
    public PaginatedResponseDTO<MovieSimpleDTO> getPopularMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> mediaPage = movieRepository.findPopularMovies(pageable);
        return movieMapper.createPaginatedResponse(mediaPage);
    }

    // Novos lançamentos
    public PaginatedResponseDTO<MovieSimpleDTO> getNewReleases(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Movie> mediaPage = movieRepository.findAll(pageable);
        return movieMapper.createPaginatedResponse(mediaPage);
    }

    // Mídias por categoria
    public PaginatedResponseDTO<MovieSimpleDTO> getMediaByCategory(Categoria categoria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Movie> mediaPage = movieRepository.findByCategoria(categoria, pageable);
        return movieMapper.createPaginatedResponse(mediaPage);
    }

    // Mídias com avaliação alta
    public PaginatedResponseDTO<MovieSimpleDTO> getHighRatedMedia(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Movie> mediaPage = movieRepository.findByAvaliacaoGreaterThanEqual(7.0, pageable);
        return movieMapper.createPaginatedResponse(mediaPage);
    }

    // Recomendações
    public PaginatedResponseDTO<MovieSimpleDTO> getRecommendations(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Movie> mediaPage = movieRepository.findRecommendations(userId, pageable);
        return movieMapper.createPaginatedResponse(mediaPage);
    }

    // Similar
    public PaginatedResponseDTO<MovieSimpleDTO> getSimilarMedia(Long mediaId, int page, int size) {
        Movie movie = movieRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Mídia não encontrada"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Movie> mediaPage = movieRepository.findSimilarMedia(movie.getCategoria(), movie.getId(), pageable);
        return movieMapper.createPaginatedResponse(mediaPage);
    }
}