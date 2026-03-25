package com.lucaflix.service;

import com.lucaflix.dto.response.movie.MovieCompleteDTO;
import com.lucaflix.dto.request.movie.MovieFilter;
import com.lucaflix.dto.mapper.MovieMapper;
import com.lucaflix.dto.response.movie.MovieSimpleDTO;
import com.lucaflix.dto.response.page.PaginatedResponseDTO;
import com.lucaflix.model.*;
import com.lucaflix.model.enums.Categories;
import com.lucaflix.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;
    private final LikeRepository likeRepository;
    private final MinhaListaRepository minhaListaRepository;
    private final UserRepository userRepository;
    private final MovieMapper movieMapper;

    // Constantes para limites de performance
    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int TOP_LIMIT = 10;

    /**
     * Valida e ajusta o tamanho da página para evitar consultas muito grandes
     */
    private int validateAndAdjustSize(int size) {
        if (size <= 0) {
            log.warn("Tamanho da página inválido: {}. Usando padrão: {}", size, DEFAULT_PAGE_SIZE);
            return DEFAULT_PAGE_SIZE;
        }
        if (size > MAX_PAGE_SIZE) {
            log.warn("Tamanho da página muito grande: {}. Limitando a: {}", size, MAX_PAGE_SIZE);
            return MAX_PAGE_SIZE;
        }
        return size;
    }

    /**
     * Valida o número da página
     */
    private int validatePage(int page) {
        return Math.max(0, page);
    }

    // Filtro de mídias - com validação de tamanho
    public PaginatedResponseDTO<MovieSimpleDTO> filtrarMedia(MovieFilter filter, int page, int size) {
        int validatedSize = validateAndAdjustSize(size);
        int validatedPage = validatePage(page);

        log.debug("Filtrando mídias - Página: {}, Tamanho: {}, Filtros: {}",
                validatedPage, validatedSize, filter);

        Pageable pageable = PageRequest.of(validatedPage, validatedSize,
                Sort.by(Sort.Direction.DESC, "dataCadastro"));

        Page<Movie> mediaPage = movieRepository.buscarPorFiltros(
                filter.getTitle(),
                filter.getAvaliacao(),
                filter.getCategories(),
                pageable
        );

        return movieMapper.createPaginatedResponse(mediaPage);
    }

    // Top 10 mais curtidas - tamanho fixo otimizado
    public List<MovieSimpleDTO> getTop10MostLiked() {
        log.debug("Buscando top 10 filmes mais curtidos");

        // Usar PageRequest com tamanho exato para performance
        List<Movie> topMovies = movieRepository.findTop10ByLikes(
                PageRequest.of(0, TOP_LIMIT)
        );

        return topMovies.stream()
                .map(movieMapper::convertToSimpleDTO)
                .collect(Collectors.toList());
    }

    public MovieCompleteDTO getMediaById(Long mediaId, UUID userId) {
        log.debug("Buscando filme por ID: {} para usuário: {}", mediaId, userId);

        Movie movie = movieRepository.findById(mediaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Filme não encontrado"
                ));
        return movieMapper.convertToCompleteDTO(movie, userId);
    }

    // Toggle Like - otimizado com transação
    @Transactional
    public boolean toggleLike(UUID userId, Long mediaId) {
        log.debug("Toggle like - Usuário: {}, Filme: {}", userId, mediaId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Movie movie = movieRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Mídia não encontrada"));

        Like existingLike = likeRepository.findByUserAndMovie(user, movie).orElse(null);

        if (existingLike != null) {
            likeRepository.delete(existingLike);
            log.debug("Like removido para usuário: {} e filme: {}", userId, mediaId);
            return false; // Removeu like
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setMovie(movie);
            likeRepository.save(like);
            log.debug("Like adicionado para usuário: {} e filme: {}", userId, mediaId);
            return true; // Adicionou like
        }
    }

    // Toggle Minha Lista - otimizado com transação
    @Transactional
    public boolean toggleMyList(UUID userId, Long mediaId) {
        log.debug("Toggle minha lista - Usuário: {}, Filme: {}", userId, mediaId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Movie movie = movieRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Mídia não encontrada"));

        MyList existingItem = minhaListaRepository.findByUserAndMovie(user, movie).orElse(null);

        if (existingItem != null) {
            minhaListaRepository.delete(existingItem);
            log.debug("Filme removido da lista para usuário: {} e filme: {}", userId, mediaId);
            return false; // Removeu da lista
        } else {
            MyList myList = new MyList();
            myList.setUser(user);
            myList.setMovie(movie);
            minhaListaRepository.save(myList);
            log.debug("Filme adicionado à lista para usuário: {} e filme: {}", userId, mediaId);
            return true; // Adicionou à lista
        }
    }

    // Mídias populares - com validação de tamanho
    public PaginatedResponseDTO<MovieSimpleDTO> getPopularMovies(int page, int size) {
        int validatedSize = validateAndAdjustSize(size);
        int validatedPage = validatePage(page);

        log.debug("Buscando filmes populares - Página: {}, Tamanho: {}", validatedPage, validatedSize);

        Pageable pageable = PageRequest.of(validatedPage, validatedSize);
        Page<Movie> mediaPage = movieRepository.findPopularMovies(pageable);

        return movieMapper.createPaginatedResponse(mediaPage);
    }

    // Novos lançamentos - com validação de tamanho
    public PaginatedResponseDTO<MovieSimpleDTO> getNewReleases(int page, int size) {
        int validatedSize = validateAndAdjustSize(size);
        int validatedPage = validatePage(page);

        log.debug("Buscando novos lançamentos - Página: {}, Tamanho: {}", validatedPage, validatedSize);

        Pageable pageable = PageRequest.of(validatedPage, validatedSize,
                Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Movie> mediaPage = movieRepository.findAll(pageable);

        return movieMapper.createPaginatedResponse(mediaPage);
    }

    // Mídias por categoria - com validação de tamanho
    public PaginatedResponseDTO<MovieSimpleDTO> getMediaByCategory(Categories categories, int page, int size) {
        int validatedSize = validateAndAdjustSize(size);
        int validatedPage = validatePage(page);

        log.debug("Buscando filmes por categoria: {} - Página: {}, Tamanho: {}",
                categories, validatedPage, validatedSize);

        Pageable pageable = PageRequest.of(validatedPage, validatedSize,
                Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Movie> mediaPage = movieRepository.findByCategoria(categories, pageable);

        return movieMapper.createPaginatedResponse(mediaPage);
    }

    // Mídias com avaliação alta - com validação de tamanho
    public PaginatedResponseDTO<MovieSimpleDTO> getHighRatedMedia(int page, int size) {
        int validatedSize = validateAndAdjustSize(size);
        int validatedPage = validatePage(page);

        log.debug("Buscando filmes com avaliação alta - Página: {}, Tamanho: {}",
                validatedPage, validatedSize);

        Pageable pageable = PageRequest.of(validatedPage, validatedSize,
                Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Movie> mediaPage = movieRepository.findByAvaliacaoGreaterThanEqual(7.0, pageable);

        return movieMapper.createPaginatedResponse(mediaPage);
    }

    // Recomendações - com validação de tamanho
    public PaginatedResponseDTO<MovieSimpleDTO> getRecommendations(UUID userId, int page, int size) {
        int validatedSize = validateAndAdjustSize(size);
        int validatedPage = validatePage(page);

        log.debug("Buscando recomendações para usuário: {} - Página: {}, Tamanho: {}",
                userId, validatedPage, validatedSize);

        Pageable pageable = PageRequest.of(validatedPage, validatedSize,
                Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Movie> mediaPage = movieRepository.findRecommendations(userId, pageable);

        return movieMapper.createPaginatedResponse(mediaPage);
    }

    // Similar - com validação de tamanho e cache do filme
    public PaginatedResponseDTO<MovieSimpleDTO> getSimilarMedia(Long mediaId, int page, int size) {
        int validatedSize = validateAndAdjustSize(size);
        int validatedPage = validatePage(page);

        log.debug("Buscando filmes similares ao ID: {} - Página: {}, Tamanho: {}",
                mediaId, validatedPage, validatedSize);

        Movie movie = movieRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Mídia não encontrada"));

        Pageable pageable = PageRequest.of(validatedPage, validatedSize,
                Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Movie> mediaPage = movieRepository.findSimilarMovies(movie.getCategories(), movie.getId(), pageable);

        return movieMapper.createPaginatedResponse(mediaPage);
    }

    /**
     * Método adicional para obter estatísticas de performance
     */
    public void logPerformanceStats(String operation, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        if (duration > 1000) { // Log apenas se demorar mais de 1 segundo
            log.warn("Operação {} demorou {} ms - considere otimização", operation, duration);
        } else {
            log.debug("Operação {} executada em {} ms", operation, duration);
        }
    }
}