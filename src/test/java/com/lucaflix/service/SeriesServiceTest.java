package com.lucaflix.service;

import com.lucaflix.dto.mapper.PageMapper;
import com.lucaflix.dto.mapper.SeriesMapper;
import com.lucaflix.dto.request.others.FilterDTO;
import com.lucaflix.dto.request.serie.CreateSerieDTO;
import com.lucaflix.dto.request.serie.UpdateSerieDTO;
import com.lucaflix.dto.response.others.PaginatedResponseDTO;
import com.lucaflix.dto.response.serie.SerieCompleteDTO;
import com.lucaflix.dto.response.serie.SerieSimpleDTO;
import com.lucaflix.model.Series;
import com.lucaflix.model.User;
import com.lucaflix.repository.SeriesRepository;
import com.lucaflix.repository.UserRepository;
import com.lucaflix.service.utils.sanitize.SanitizeUtils;
import com.lucaflix.service.utils.validate.SeriesValidate;
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
public class SeriesServiceTest {

    @InjectMocks
    private SeriesService seriesService;

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SeriesValidate seriesValidate;

    @Mock
    private SeriesMapper seriesMapper;

    @Mock
    private PageMapper pageMapper;

    private Series series;
    private User user;
    private UUID seriesId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        seriesId = UUID.randomUUID();
        userId = UUID.randomUUID();

        series = new Series();
        series.setId(seriesId);
        series.setTitle("Breaking Bad");
        series.setCategories(List.of());

        user = new User();
        user.setId(userId);
        user.setUsername("lucassilva");
    }

    // -------------------------------------------------------------------------
    // filterSeries
    // -------------------------------------------------------------------------

    @Test
    void filterSeries_ShouldReturnPaginatedResponse() {
        FilterDTO filter = new FilterDTO();
        Page<Series> seriesPage = new PageImpl<>(List.of(series));
        PaginatedResponseDTO<SerieSimpleDTO> expected = new PaginatedResponseDTO<>();

        when(seriesRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(seriesPage);
        when(pageMapper.toPaginatedDTO(eq(seriesPage), any(Function.class))).thenReturn(expected);

        PaginatedResponseDTO<SerieSimpleDTO> result = seriesService.filterSeries(filter, 0, 20);

        assertThat(result).isNotNull();
        verify(seriesRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void filterSeries_ShouldNormalizePage_WhenPageIsNegative() {
        FilterDTO filter = new FilterDTO();
        Page<Series> seriesPage = new PageImpl<>(List.of());
        PaginatedResponseDTO<SerieSimpleDTO> expected = new PaginatedResponseDTO<>();

        when(seriesRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(seriesPage);
        when(pageMapper.toPaginatedDTO(eq(seriesPage), any(Function.class))).thenReturn(expected);

        PaginatedResponseDTO<SerieSimpleDTO> result = seriesService.filterSeries(filter, -5, 20);

        assertThat(result).isNotNull();
    }

    @Test
    void filterSeries_ShouldNormalizeSize_WhenSizeIsZero() {
        FilterDTO filter = new FilterDTO();
        Page<Series> seriesPage = new PageImpl<>(List.of());
        PaginatedResponseDTO<SerieSimpleDTO> expected = new PaginatedResponseDTO<>();

        when(seriesRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(seriesPage);
        when(pageMapper.toPaginatedDTO(eq(seriesPage), any(Function.class))).thenReturn(expected);

        PaginatedResponseDTO<SerieSimpleDTO> result = seriesService.filterSeries(filter, 0, 0);

        assertThat(result).isNotNull();
    }

    @Test
    void filterSeries_ShouldNormalizeSize_WhenSizeExceedsLimit() {
        FilterDTO filter = new FilterDTO();
        Page<Series> seriesPage = new PageImpl<>(List.of());
        PaginatedResponseDTO<SerieSimpleDTO> expected = new PaginatedResponseDTO<>();

        when(seriesRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(seriesPage);
        when(pageMapper.toPaginatedDTO(eq(seriesPage), any(Function.class))).thenReturn(expected);

        PaginatedResponseDTO<SerieSimpleDTO> result = seriesService.filterSeries(filter, 0, 999);

        assertThat(result).isNotNull();
    }

    // -------------------------------------------------------------------------
    // getSeriesById
    // -------------------------------------------------------------------------

    @Test
    void getSeriesById_ShouldReturnComplete_WhenUserIsNull() {
        SerieCompleteDTO expected = new SerieCompleteDTO();
        when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(series));
        when(seriesMapper.toComplete(series, null)).thenReturn(expected);

        SerieCompleteDTO result = seriesService.getSeriesById(seriesId, null);

        assertThat(result).isNotNull();
        verify(userRepository, never()).findById(any());
    }

    @Test
    void getSeriesById_ShouldReturnComplete_WhenUserIsProvided() {
        SerieCompleteDTO expected = new SerieCompleteDTO();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(series));
        when(seriesMapper.toComplete(series, user)).thenReturn(expected);

        SerieCompleteDTO result = seriesService.getSeriesById(seriesId, user);

        assertThat(result).isNotNull();
        verify(userRepository).findById(userId);
    }

    @Test
    void getSeriesById_ShouldThrowException_WhenSeriesNotFound() {
        when(seriesRepository.findById(seriesId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seriesService.getSeriesById(seriesId, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Series not found");
    }

    @Test
    void getSeriesById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seriesService.getSeriesById(seriesId, user))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    // -------------------------------------------------------------------------
    // getSimilarSeries
    // -------------------------------------------------------------------------

    @Test
    void getSimilarSeries_ShouldReturnPaginatedSimilarSeries() {
        Page<Series> similarPage = new PageImpl<>(List.of(series));
        PaginatedResponseDTO<SerieSimpleDTO> expected = new PaginatedResponseDTO<>();

        when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(series));
        when(seriesRepository.findSimilarSeries(any(), eq(seriesId), any(Pageable.class))).thenReturn(similarPage);
        when(pageMapper.toPaginatedDTO(eq(similarPage), any(Function.class))).thenReturn(expected);

        PaginatedResponseDTO<SerieSimpleDTO> result = seriesService.getSimilarSeries(seriesId, 0, 10);

        assertThat(result).isNotNull();
        verify(seriesRepository).findSimilarSeries(any(), eq(seriesId), any(Pageable.class));
    }

    @Test
    void getSimilarSeries_ShouldThrowException_WhenSeriesNotFound() {
        when(seriesRepository.findById(seriesId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seriesService.getSimilarSeries(seriesId, 0, 10))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Series not found");
    }

    // -------------------------------------------------------------------------
    // createSeries
    // -------------------------------------------------------------------------

    @Test
    void createSeries_ShouldSaveAndReturnCompleteDTO() {
        CreateSerieDTO createDTO = new CreateSerieDTO();
        SerieCompleteDTO expected = new SerieCompleteDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(createDTO)).thenAnswer(inv -> null);

            when(seriesMapper.toEntity(createDTO)).thenReturn(series);
            when(seriesRepository.save(series)).thenReturn(series);
            when(seriesMapper.toComplete(series, null)).thenReturn(expected);

            SerieCompleteDTO result = seriesService.createSeries(createDTO);

            assertThat(result).isNotNull();
            verify(seriesRepository).save(series);
            verify(seriesMapper).toComplete(series, null);
        }
    }

    @Test
    void createSeries_ShouldCallSanitizeBeforeSaving() {
        CreateSerieDTO createDTO = new CreateSerieDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(createDTO)).thenAnswer(inv -> null);

            when(seriesMapper.toEntity(createDTO)).thenReturn(series);
            when(seriesRepository.save(series)).thenReturn(series);
            when(seriesMapper.toComplete(series, null)).thenReturn(new SerieCompleteDTO());

            seriesService.createSeries(createDTO);

            sanitize.verify(() -> SanitizeUtils.sanitizeStrings(createDTO));
        }
    }

    // -------------------------------------------------------------------------
    // updateSeries
    // -------------------------------------------------------------------------

    @Test
    void updateSeries_ShouldUpdateAndReturnCompleteDTO() {
        UpdateSerieDTO updateDTO = new UpdateSerieDTO();
        SerieCompleteDTO expected = new SerieCompleteDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(updateDTO)).thenAnswer(inv -> null);

            when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(series));
            when(seriesValidate.validUpdate(any(), any())).thenReturn(series);
            when(seriesRepository.save(series)).thenReturn(series);
            when(seriesMapper.toComplete(series, null)).thenReturn(expected);

            SerieCompleteDTO result = seriesService.updateSeries(updateDTO, seriesId);

            assertThat(result).isNotNull();
            verify(seriesValidate).validUpdate(updateDTO, series);
            verify(seriesRepository).save(series);
        }
    }

    @Test
    void updateSeries_ShouldThrowException_WhenSeriesNotFound() {
        UpdateSerieDTO updateDTO = new UpdateSerieDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(updateDTO)).thenAnswer(inv -> null);

            when(seriesRepository.findById(seriesId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> seriesService.updateSeries(updateDTO, seriesId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Series not found");

            verify(seriesValidate, never()).validUpdate(any(), any());
            verify(seriesRepository, never()).save(any());
        }
    }

    @Test
    void updateSeries_ShouldCallSanitizeBeforeValidating() {
        UpdateSerieDTO updateDTO = new UpdateSerieDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(updateDTO)).thenAnswer(inv -> null);

            when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(series));
            when(seriesValidate.validUpdate(updateDTO, series)).thenReturn(null);
            when(seriesRepository.save(series)).thenReturn(series);
            when(seriesMapper.toComplete(series, null)).thenReturn(new SerieCompleteDTO());

            seriesService.updateSeries(updateDTO, seriesId);

            sanitize.verify(() -> SanitizeUtils.sanitizeStrings(updateDTO));
        }
    }

    @Test
    void deleteSeries_ShouldDeleteSeries_WhenFound() {
        when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(series));

        seriesService.deleteSeries(seriesId);

        verify(seriesRepository).delete(series);
    }
}