package com.lucaflix.service;

import com.lucaflix.dto.mapper.MovieMapper;
import com.lucaflix.dto.mapper.PageMapper;
import com.lucaflix.dto.request.movie.CreateMovieDTO;
import com.lucaflix.dto.request.movie.UpdateMovieDTO;
import com.lucaflix.dto.request.others.FilterDTO;
import com.lucaflix.dto.response.movie.MovieCompleteDTO;
import com.lucaflix.dto.response.movie.MovieSimpleDTO;
import com.lucaflix.dto.response.others.PaginatedResponseDTO;
import com.lucaflix.model.Movie;
import com.lucaflix.model.User;
import com.lucaflix.repository.LikeRepository;
import com.lucaflix.repository.MovieRepository;
import com.lucaflix.repository.MyListRepository;
import com.lucaflix.repository.UserRepository;
import com.lucaflix.service.validate.MovieValidate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieValidate movieValidate;
    private final LikeRepository likeRepository;
    private final MyListRepository myListRepository;
    private final MovieMapper movieMapper;
    private final PageMapper pageMapper;
    private final UserRepository userRepository;

    public PaginatedResponseDTO<MovieSimpleDTO> getMoviesFilter(FilterDTO filter, int page, int size) {
        if (page <= 0 || page > 100) {
            page = 0;
        }
        if (size <= 0 || size > 100) {
            size = 30;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dateRegistered"));

        Page<Movie> mediaPage = movieRepository.buscarPorFiltros(filter.getTitle(), filter.getRating(), filter.getCategories(), pageable);
        return pageMapper.toPaginatedDTO(mediaPage, movie -> movieMapper.toSimple(movie, null));
    }

    public MovieCompleteDTO getMediaById(UUID mediaId, User userRequest) {

        User user = userRepository.findById(userRequest.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        Movie movie = movieRepository.findById(mediaId).orElseThrow(() -> new RuntimeException("Movie not found"));
        return movieMapper.toComplete(movie, user);
    }

    public PaginatedResponseDTO<MovieSimpleDTO> getSimilarMedia(UUID mediaId, int page, int size) {
        if (page <= 0 || page > 100) {
            page = 0;
        }
        if (size <= 0 || size > 100) {
            size = 30;
        }
        Movie movie = movieRepository.findById(mediaId).orElseThrow(() -> new RuntimeException("Movie not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rating"));
        Page<Movie> mediaPage = movieRepository.findSimilarMovies(movie.getCategories(), movie.getId(), pageable);

        return pageMapper.toPaginatedDTO(mediaPage, media -> movieMapper.toSimple(media, null));
    }

    public MovieCompleteDTO createMovie(CreateMovieDTO movieRequest){
        Movie movie = movieMapper.toMovie(movieRequest);

        movieRepository.save(movie);

        return movieMapper.toComplete(movie, null);
    }

    public MovieCompleteDTO updateMovie(UpdateMovieDTO dto, UUID id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        movieValidate.validUpdate(dto, movie);
        movieRepository.save(movie);

        return movieMapper.toComplete(movie, null);
    }

    @Transactional
    public void deleteMovie(UUID id) {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new RuntimeException("Movie not found"));

        likeRepository.deleteByMovie(movie);
        myListRepository.deleteByMovie(movie);

        movieRepository.deleteById(id);
    }
}