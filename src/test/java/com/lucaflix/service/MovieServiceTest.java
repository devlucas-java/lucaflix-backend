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
import com.lucaflix.repository.MovieRepository;
import com.lucaflix.repository.UserRepository;
import com.lucaflix.service.utils.sanitize.SanitizeUtils;
import com.lucaflix.service.utils.validate.MovieValidate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @InjectMocks
    private MovieService movieService;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieValidate movieValidate;

    @Mock
    private MovieMapper movieMapper;

    @Mock
    private PageMapper pageMapper;

    @Mock
    private UserRepository userRepository;

    private Movie movie;
    private User user;
    private UUID movieId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        movieId = UUID.randomUUID();
        userId = UUID.randomUUID();

        movie = new Movie();
        movie.setId(movieId);
        movie.setTitle("Inception");
        movie.setCategories(List.of());

        user = new User();
        user.setId(userId);
        user.setUsername("lucassilva");
    }

    // -------------------------------------------------------------------------
    // filterMovies
    // -------------------------------------------------------------------------

    @Test
    void filterMovies_ShouldReturnPaginatedResponse() {
        FilterDTO filter = new FilterDTO();
        Page<Movie> moviePage = new PageImpl<>(List.of(movie));
        PaginatedResponseDTO<MovieSimpleDTO> expected = new PaginatedResponseDTO<>();

        when(movieRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(moviePage);
        when(pageMapper.toPaginatedDTO(eq(moviePage), any(Function.class))).thenReturn(expected);

        PaginatedResponseDTO<MovieSimpleDTO> result = movieService.filterMovies(filter, 0, 20);

        assertThat(result).isNotNull();
        verify(movieRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void filterMovies_ShouldNormalizePage_WhenPageIsNegative() {
        FilterDTO filter = new FilterDTO();
        Page<Movie> moviePage = new PageImpl<>(List.of());
        PaginatedResponseDTO<MovieSimpleDTO> expected = new PaginatedResponseDTO<>();

        when(movieRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(moviePage);
        when(pageMapper.toPaginatedDTO(eq(moviePage), any(Function.class))).thenReturn(expected);

        PaginatedResponseDTO<MovieSimpleDTO> result = movieService.filterMovies(filter, -3, 20);

        assertThat(result).isNotNull();
    }

    @Test
    void filterMovies_ShouldNormalizeSize_WhenSizeIsZeroOrExceedsLimit() {
        FilterDTO filter = new FilterDTO();
        Page<Movie> moviePage = new PageImpl<>(List.of());
        PaginatedResponseDTO<MovieSimpleDTO> expected = new PaginatedResponseDTO<>();

        when(movieRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(moviePage);
        when(pageMapper.toPaginatedDTO(eq(moviePage), any(Function.class))).thenReturn(expected);

        assertThat(movieService.filterMovies(filter, 0, 0)).isNotNull();
        assertThat(movieService.filterMovies(filter, 0, 500)).isNotNull();
    }

    // -------------------------------------------------------------------------
    // getMediaById
    // -------------------------------------------------------------------------

    @Test
    void getMediaById_ShouldReturnCompleteDTO_WhenUserIsNull() {
        MovieCompleteDTO expected = new MovieCompleteDTO();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(movieMapper.toComplete(movie, null)).thenReturn(expected);

        MovieCompleteDTO result = movieService.getMediaById(movieId, null);

        assertThat(result).isNotNull();
        verify(userRepository, never()).findById(any());
    }

    @Test
    void getMediaById_ShouldReturnCompleteDTO_WhenUserIsProvided() {
        MovieCompleteDTO expected = new MovieCompleteDTO();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(movieMapper.toComplete(movie, user)).thenReturn(expected);

        MovieCompleteDTO result = movieService.getMediaById(movieId, user);

        assertThat(result).isNotNull();
        verify(userRepository).findById(userId);
    }

    @Test
    void getMediaById_ShouldThrowException_WhenMovieNotFound() {
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.getMediaById(movieId, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Movie not found");
    }

    @Test
    void getMediaById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.getMediaById(movieId, user))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    // -------------------------------------------------------------------------
    // getSimilarMedia
    // -------------------------------------------------------------------------

    @Test
    void getSimilarMedia_ShouldReturnPaginatedSimilarMovies() {
        Page<Movie> similarPage = new PageImpl<>(List.of(movie));
        PaginatedResponseDTO<MovieSimpleDTO> expected = new PaginatedResponseDTO<>();

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(movieRepository.findSimilarMovie(any(), eq(movieId), any(Pageable.class))).thenReturn(similarPage);
        when(pageMapper.toPaginatedDTO(eq(similarPage), any(Function.class))).thenReturn(expected);

        PaginatedResponseDTO<MovieSimpleDTO> result = movieService.getSimilarMedia(movieId, 0, 10);

        assertThat(result).isNotNull();
        verify(movieRepository).findSimilarMovie(any(), eq(movieId), any(Pageable.class));
    }

    @Test
    void getSimilarMedia_ShouldThrowException_WhenMovieNotFound() {
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.getSimilarMedia(movieId, 0, 10))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Movie not found");
    }

    @Test
    void getSimilarMedia_ShouldNormalizePageAndSize_WhenInvalid() {
        Page<Movie> similarPage = new PageImpl<>(List.of());
        PaginatedResponseDTO<MovieSimpleDTO> expected = new PaginatedResponseDTO<>();

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(movieRepository.findSimilarMovie(any(), any(), any(Pageable.class))).thenReturn(similarPage);
        when(pageMapper.toPaginatedDTO(eq(similarPage), any(Function.class))).thenReturn(expected);

        // page <= 0 e size > 100 devem ser normalizados
        PaginatedResponseDTO<MovieSimpleDTO> result = movieService.getSimilarMedia(movieId, -1, 999);

        assertThat(result).isNotNull();
    }

    // -------------------------------------------------------------------------
    // createMovie
    // -------------------------------------------------------------------------

    @Test
    void createMovie_ShouldSaveAndReturnCompleteDTO() {
        CreateMovieDTO createDTO = new CreateMovieDTO();
        MovieCompleteDTO expected = new MovieCompleteDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(createDTO)).thenAnswer(inv -> null);

            when(movieMapper.toMovie(createDTO)).thenReturn(movie);
            when(movieRepository.save(movie)).thenReturn(movie);
            when(movieMapper.toComplete(movie, null)).thenReturn(expected);

            MovieCompleteDTO result = movieService.createMovie(createDTO);

            assertThat(result).isNotNull();
            verify(movieRepository).save(movie);
            sanitize.verify(() -> SanitizeUtils.sanitizeStrings(createDTO));
        }
    }

    // -------------------------------------------------------------------------
    // updateMovie
    // -------------------------------------------------------------------------

    @Test
    void updateMovie_ShouldUpdateAndReturnCompleteDTO() {
        UpdateMovieDTO updateDTO = new UpdateMovieDTO();
        MovieCompleteDTO expected = new MovieCompleteDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(updateDTO)).thenAnswer(inv -> null);

            when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
            when(movieValidate.validUpdate(updateDTO, movie)).thenReturn(null);
            when(movieRepository.save(movie)).thenReturn(movie);
            when(movieMapper.toComplete(movie, null)).thenReturn(expected);

            MovieCompleteDTO result = movieService.updateMovie(updateDTO, movieId);

            assertThat(result).isNotNull();
            verify(movieValidate).validUpdate(updateDTO, movie);
            verify(movieRepository).save(movie);
        }
    }

    @Test
    void updateMovie_ShouldThrowException_WhenMovieNotFound() {
        UpdateMovieDTO updateDTO = new UpdateMovieDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(updateDTO)).thenAnswer(inv -> null);

            when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> movieService.updateMovie(updateDTO, movieId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Movie not found");

            verify(movieValidate, never()).validUpdate(any(), any());
            verify(movieRepository, never()).save(any());
        }
    }

    // -------------------------------------------------------------------------
    // deleteMovie
    // -------------------------------------------------------------------------

    @Test
    void deleteMovie_ShouldDeleteMovie_WhenFound() {
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));

        movieService.deleteMovie(movieId);

        verify(movieRepository).deleteById(movieId);
    }

    @Test
    void deleteMovie_ShouldThrowException_WhenMovieNotFound() {
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movieService.deleteMovie(movieId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Movie not found");

        verify(movieRepository, never()).deleteById(any());
    }
}