package com.lucaflix.service;

import com.lucaflix.dto.mapper.EpisodeMapper;
import com.lucaflix.dto.mapper.SeasonMapper;
import com.lucaflix.dto.request.serie.CreateSeasonDTO;
import com.lucaflix.dto.request.serie.UpdateSeasonDTO;
import com.lucaflix.dto.response.serie.SeasonDTO;
import com.lucaflix.model.Episode;
import com.lucaflix.model.Season;
import com.lucaflix.model.Series;
import com.lucaflix.repository.SeasonRepository;
import com.lucaflix.repository.SeriesRepository;
import com.lucaflix.service.utils.sanitize.SanitizeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SeasonServiceTest {

    @InjectMocks
    private SeasonService seasonService;

    @Mock
    private SeasonRepository seasonRepository;

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private SeasonMapper seasonMapper;

    @Mock
    private EpisodeMapper episodeMapper;

    private Season season;
    private Series series;
    private SeasonDTO seasonDTO;
    private UUID seriesId;
    private Long seasonId;

    @BeforeEach
    void setUp() {
        seriesId = UUID.randomUUID();
        seasonId = 1L;

        series = new Series();
        series.setId(seriesId);
        series.setTitle("Breaking Bad");

        season = new Season();
        season.setId(seasonId);
        season.setSeries(series);
        season.setNumberSeason(1);
        season.setYearRelease(2008);
        season.setEpisodes(Set.of());
        season.setTotalEpisodes(0);

        seasonDTO = new SeasonDTO();
        seasonDTO.setId(seasonId);
    }

    // -------------------------------------------------------------------------
    // getSeason
    // -------------------------------------------------------------------------

    @Test
    void getSeason_ShouldReturnSeasonDTO_WhenSeasonExists() {
        when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));
        when(seasonMapper.toDTO(season)).thenReturn(seasonDTO);

        SeasonDTO result = seasonService.getSeason(seasonId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(seasonId);
        verify(seasonRepository).findById(seasonId);
        verify(seasonMapper).toDTO(season);
    }

    @Test
    void getSeason_ShouldThrowException_WhenSeasonNotFound() {
        when(seasonRepository.findById(seasonId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seasonService.getSeason(seasonId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Season not found");

        verify(seasonMapper, never()).toDTO(any());
    }

    // -------------------------------------------------------------------------
    // createSeason
    // -------------------------------------------------------------------------

    @Test
    void createSeason_ShouldSaveAndReturnSeasonDTO() {
        CreateSeasonDTO createDTO = new CreateSeasonDTO();
        createDTO.setNumberSeason(1);
        createDTO.setYearRelease(2008);
        createDTO.setEpisodes(List.of());

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(any())).thenAnswer(inv -> null);

            when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(series));
            when(seasonRepository.save(any(Season.class))).thenAnswer(inv -> inv.getArgument(0));
            when(seasonMapper.toDTO(any(Season.class))).thenReturn(seasonDTO);

            SeasonDTO result = seasonService.createSeason(createDTO, seriesId);

            assertThat(result).isNotNull();
            verify(seriesRepository).findById(seriesId);
            verify(seasonRepository).save(any(Season.class));
            verify(seasonMapper).toDTO(any(Season.class));
        }
    }

    @Test
    void createSeason_ShouldSetSeriesOnSeason() {
        CreateSeasonDTO createDTO = new CreateSeasonDTO();
        createDTO.setNumberSeason(2);
        createDTO.setYearRelease(2009);
        createDTO.setEpisodes(List.of());

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(any())).thenAnswer(inv -> null);

            when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(series));
            when(seasonRepository.save(any(Season.class))).thenAnswer(inv -> {
                Season saved = inv.getArgument(0);
                assertThat(saved.getSeries()).isEqualTo(series);
                assertThat(saved.getNumberSeason()).isEqualTo(2);
                assertThat(saved.getYearRelease()).isEqualTo(2009);
                return saved;
            });
            when(seasonMapper.toDTO(any(Season.class))).thenReturn(seasonDTO);

            seasonService.createSeason(createDTO, seriesId);

            verify(seasonRepository).save(any(Season.class));
        }
    }

    @Test
    void createSeason_ShouldThrowException_WhenSeriesNotFound() {
        CreateSeasonDTO createDTO = new CreateSeasonDTO();
        createDTO.setEpisodes(List.of());

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            when(seriesRepository.findById(seriesId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> seasonService.createSeason(createDTO, seriesId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Series not found");

            verify(seasonRepository, never()).save(any());
        }
    }

    @Test
    void createSeason_ShouldCallSanitizeOnDTOAndEachEpisode() {
        var epDTO1 = new com.lucaflix.dto.request.serie.CreateEpisodeDTO();
        var epDTO2 = new com.lucaflix.dto.request.serie.CreateEpisodeDTO();

        CreateSeasonDTO createDTO = new CreateSeasonDTO();
        createDTO.setNumberSeason(1);
        createDTO.setYearRelease(2008);
        createDTO.setEpisodes(List.of(epDTO1, epDTO2));

        Episode ep1 = new Episode();
        Episode ep2 = new Episode();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(any())).thenAnswer(inv -> null);

            when(seriesRepository.findById(seriesId)).thenReturn(Optional.of(series));
            when(episodeMapper.toEntity(epDTO1)).thenReturn(ep1);
            when(episodeMapper.toEntity(epDTO2)).thenReturn(ep2);
            when(seasonRepository.save(any(Season.class))).thenAnswer(inv -> inv.getArgument(0));
            when(seasonMapper.toDTO(any(Season.class))).thenReturn(seasonDTO);

            seasonService.createSeason(createDTO, seriesId);

            // 1x para o createDTO + 2x para cada episódio = 3 chamadas
            sanitize.verify(() -> SanitizeUtils.sanitizeStrings(any()), times(3));
        }
    }

    // -------------------------------------------------------------------------
    // updateSeason
    // -------------------------------------------------------------------------

    @Test
    void updateSeason_ShouldUpdateFieldsAndReturnSeasonDTO() {
        UpdateSeasonDTO updateDTO = new UpdateSeasonDTO();
        updateDTO.setNumberSeason(3);
        updateDTO.setYearRelease(2010);

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(updateDTO)).thenAnswer(inv -> null);

            when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));
            when(seasonMapper.toDTO(season)).thenReturn(seasonDTO);

            SeasonDTO result = seasonService.updateSeason(updateDTO, seasonId);

            assertThat(result).isNotNull();
            assertThat(season.getNumberSeason()).isEqualTo(3);
            assertThat(season.getYearRelease()).isEqualTo(2010);
            verify(seasonMapper).toDTO(season);
        }
    }

    @Test
    void updateSeason_ShouldCallSanitize() {
        UpdateSeasonDTO updateDTO = new UpdateSeasonDTO();
        updateDTO.setNumberSeason(1);
        updateDTO.setYearRelease(2008);

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(updateDTO)).thenAnswer(inv -> null);

            when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));
            when(seasonMapper.toDTO(season)).thenReturn(seasonDTO);

            seasonService.updateSeason(updateDTO, seasonId);

            sanitize.verify(() -> SanitizeUtils.sanitizeStrings(updateDTO));
        }
    }

    @Test
    void updateSeason_ShouldThrowException_WhenSeasonNotFound() {
        UpdateSeasonDTO updateDTO = new UpdateSeasonDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(updateDTO)).thenAnswer(inv -> null);

            when(seasonRepository.findById(seasonId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> seasonService.updateSeason(updateDTO, seasonId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Season not found");

            verify(seasonMapper, never()).toDTO(any());
        }
    }

    // -------------------------------------------------------------------------
    // deleteSeason
    // -------------------------------------------------------------------------

    @Test
    void deleteSeason_ShouldDeleteSeason_WhenFound() {
        when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));

        seasonService.deleteSeason(seasonId);

        verify(seasonRepository).delete(season);
    }

    @Test
    void deleteSeason_ShouldThrowException_WhenSeasonNotFound() {
        when(seasonRepository.findById(seasonId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seasonService.deleteSeason(seasonId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Season not found");

        verify(seasonRepository, never()).delete(any());
    }
}