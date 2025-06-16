package com.lucaflix.service;

import com.lucaflix.dto.media.MovieCompleteDTO;
import com.lucaflix.dto.media.MovieFilter;
import com.lucaflix.dto.media.MovieSimpleDTO;
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

    // Filtro de mídias
    public PaginatedResponseDTO<MovieSimpleDTO> filtrarMedia(MovieFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Movie> mediaPage = movieRepository.buscarPorFiltros(
                filter.getTitle(),
                filter.getAvaliacao(),
                filter.getCategoria(),
                pageable
        );
        return createPaginatedResponse(mediaPage);
    }

    // Top 10 mais curtidas
    public List<MovieSimpleDTO> getTop10MostLiked() {
        List<Movie> topMovies = movieRepository.findTop10ByLikes(PageRequest.of(0, 10));
        return topMovies.stream().map(this::convertToSimpleDTO).collect(Collectors.toList());
    }

    public MovieCompleteDTO getMediaById(Long mediaId, UUID userId) {
        Movie movie = movieRepository.findById(mediaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Filme não encontrado"
                ));
        return convertToCompleteDTO(movie, userId);
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

    // Minha lista do usuário
    public PaginatedResponseDTO<MovieSimpleDTO> getMyList(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("dataAdicao").descending());
        Page<MinhaLista> myListPage = minhaListaRepository.findByUser(user, pageable);

        List<MovieSimpleDTO> mediaList = myListPage.getContent().stream()
                .filter(item -> item.getMovie() != null)
                .map(item -> convertToSimpleDTO(item.getMovie()))
                .collect(Collectors.toList());

        return new PaginatedResponseDTO<>(
                mediaList,
                myListPage.getNumber(),
                myListPage.getTotalPages(),
                myListPage.getTotalElements(),
                myListPage.getSize(),
                myListPage.isFirst(),
                myListPage.isLast(),
                myListPage.hasNext(),
                myListPage.hasPrevious()
        );
    }

    // Mídias populares (mais curtidas)
    public PaginatedResponseDTO<MovieSimpleDTO> getPopularMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> mediaPage = movieRepository.findPopularMovies(pageable);
        return createPaginatedResponse(mediaPage);
    }

    // Novos lançamentos
    public PaginatedResponseDTO<MovieSimpleDTO> getNewReleases(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Movie> mediaPage = movieRepository.findAll(pageable);
        return createPaginatedResponse(mediaPage);
    }

    // Mídias por categoria
    public PaginatedResponseDTO<MovieSimpleDTO> getMediaByCategory(Categoria categoria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Movie> mediaPage = movieRepository.findByCategoria(categoria, pageable);
        return createPaginatedResponse(mediaPage);
    }

    // Mídias com avaliação alta
    public PaginatedResponseDTO<MovieSimpleDTO> getHighRatedMedia(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Movie> mediaPage = movieRepository.findByAvaliacaoGreaterThanEqual(7.0, pageable);
        return createPaginatedResponse(mediaPage);
    }

    // Recomendações
    public PaginatedResponseDTO<MovieSimpleDTO> getRecommendations(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Movie> mediaPage = movieRepository.findRecommendations(userId, pageable);
        return createPaginatedResponse(mediaPage);
    }

    // Similar
    public PaginatedResponseDTO<MovieSimpleDTO> getSimilarMedia(Long mediaId, int page, int size) {
        Movie movie = movieRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Mídia não encontrada"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Movie> mediaPage = movieRepository.findSimilarMedia(movie.getCategoria(), movie.getId(), pageable);
        return createPaginatedResponse(mediaPage);
    }

    // Conversores
    private MovieSimpleDTO convertToSimpleDTO(Movie movie) {
        MovieSimpleDTO dto = new MovieSimpleDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setAnoLancamento(movie.getAnoLancamento());
        dto.setDuracaoMinutos(movie.getDuracaoMinutos());
        dto.setTmdbId(movie.getTmdbId());
        dto.setImdbId(movie.getImdbId());
        dto.setPaisOrigen(movie.getPaisOrigen());
        dto.setCategoria(movie.getCategoria());
        dto.setMinAge(movie.getMinAge());
        dto.setAvaliacao(movie.getAvaliacao());
        dto.setImageURL1(movie.getImageURL1());
        dto.setImageURL2(movie.getImageURL2());
        dto.setTotalLikes((long) (movie.getLikes() != null ? movie.getLikes().size() : 0));
        return dto;
    }

    private MovieCompleteDTO convertToCompleteDTO(Movie movie, UUID userId) {
        MovieCompleteDTO dto = new MovieCompleteDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setAnoLancamento(movie.getAnoLancamento());
        dto.setDuracaoMinutos(movie.getDuracaoMinutos());
        dto.setTmdbId(movie.getTmdbId());
        dto.setImdbId(movie.getImdbId());
        dto.setPaisOrigen(movie.getPaisOrigen());
        dto.setSinopse(movie.getSinopse());
        dto.setDataCadastro(movie.getDataCadastro());
        dto.setCategoria(movie.getCategoria());
        dto.setMinAge(movie.getMinAge());
        dto.setAvaliacao(movie.getAvaliacao());
        dto.setEmbed1(movie.getEmbed1());
        dto.setEmbed2(movie.getEmbed2());
        dto.setTrailer(movie.getTrailer());
        dto.setImageURL1(movie.getImageURL1());
        dto.setImageURL2(movie.getImageURL2());
        dto.setTotalLikes((long) (movie.getLikes() != null ? movie.getLikes().size() : 0));

        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                dto.setUserLiked(likeRepository.existsByUserAndMovie(user, movie));
                dto.setInUserList(minhaListaRepository.existsByUserAndMovie(user, movie));
            }
        } else if (userId == null) {
            dto.setInUserList(null);
            dto.setUserLiked(null);

        }
        return dto;
    }

    private PaginatedResponseDTO<MovieSimpleDTO> createPaginatedResponse(Page<Movie> mediaPage) {
        List<MovieSimpleDTO> mediaDTOs = mediaPage.getContent().stream()
                .map(this::convertToSimpleDTO)
                .collect(Collectors.toList());

        return new PaginatedResponseDTO<>(
                mediaDTOs,
                mediaPage.getNumber(),
                mediaPage.getTotalPages(),
                mediaPage.getTotalElements(),
                mediaPage.getSize(),
                mediaPage.isFirst(),
                mediaPage.isLast(),
                mediaPage.hasNext(),
                mediaPage.hasPrevious()
        );
    }
}