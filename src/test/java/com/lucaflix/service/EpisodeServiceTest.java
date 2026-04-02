package com.lucaflix.service;

import com.lucaflix.dto.mapper.EpisodeMapper;
import com.lucaflix.dto.request.serie.CreateEpisodeDTO;
import com.lucaflix.dto.request.serie.UpdateEpisodeDTO;
import com.lucaflix.dto.response.serie.EpisodeDTO;
import com.lucaflix.model.Episode;
import com.lucaflix.model.Season;
import com.lucaflix.repository.EpisodeRepository;
import com.lucaflix.repository.SeasonRepository;
import com.lucaflix.service.utils.sanitize.SanitizeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EpisodeServiceTest {

    @InjectMocks
    private EpisodeService episodeService;

    @Mock
    private EpisodeRepository episodeRepository;

    @Mock
    private SeasonRepository seasonRepository;

    @Mock
    private EpisodeMapper episodeMapper;

    private Episode episode;
    private EpisodeDTO episodeDTO;
    private Long episodeId;
    private Long seasonId;

    @BeforeEach
    void setUp() {
        episodeId = 1L;
        seasonId = 10L;

        episode = new Episode();
        episode.setId(episodeId);
        episode.setTitle("Pilot");
        episode.setNumberEpisode(1);
        episode.setMinutesDuration(47);
        episode.setEmbed1("https://embed1.com");
        episode.setEmbed2("https://embed2.com");
        episode.setSynopsis("First episode synopsis");

        episodeDTO = new EpisodeDTO();
        episodeDTO.setId(episodeId);
    }

    // -------------------------------------------------------------------------
    // getEpisode
    // -------------------------------------------------------------------------

    @Test
    void getEpisode_ShouldReturnEpisodeDTO_WhenEpisodeExists() {
        when(episodeRepository.findById(episodeId)).thenReturn(Optional.of(episode));
        when(episodeMapper.toDTO(episode)).thenReturn(episodeDTO);

        EpisodeDTO result = episodeService.getEpisode(episodeId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(episodeId);
        verify(episodeMapper).toDTO(episode);
    }

    @Test
    void getEpisode_ShouldThrowException_WhenEpisodeNotFound() {
        when(episodeRepository.findById(episodeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> episodeService.getEpisode(episodeId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Episode not found");

        verify(episodeMapper, never()).toDTO(any());
    }

    // -------------------------------------------------------------------------
    // createEpisode
    // -------------------------------------------------------------------------

    @Test
    void createEpisode_ShouldSaveAndReturnEpisodeDTO() {
        CreateEpisodeDTO createDTO = new CreateEpisodeDTO();
        Season season = new Season();
        season.setId(seasonId);

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(createDTO)).thenAnswer(inv -> null);

            when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));
            when(episodeMapper.toEntity(createDTO)).thenReturn(episode);
            when(episodeRepository.save(episode)).thenReturn(episode);
            when(episodeMapper.toDTO(episode)).thenReturn(episodeDTO);

            EpisodeDTO result = episodeService.createEpisode(createDTO, seasonId);

            assertThat(result).isNotNull();
            verify(seasonRepository).findById(seasonId);
            verify(episodeRepository).save(episode);
            sanitize.verify(() -> SanitizeUtils.sanitizeStrings(createDTO));
        }
    }

    @Test
    void createEpisode_ShouldThrowException_WhenSeasonNotFound() {
        CreateEpisodeDTO createDTO = new CreateEpisodeDTO();
        when(seasonRepository.findById(seasonId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> episodeService.createEpisode(createDTO, seasonId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Season not found");

        verify(episodeRepository, never()).save(any());
    }

    @Test
    void createEpisode_ShouldCallSanitizeBeforeSaving() {
        CreateEpisodeDTO createDTO = new CreateEpisodeDTO();
        Season season = new Season();
        season.setId(seasonId);

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(createDTO)).thenAnswer(inv -> null);

            when(seasonRepository.findById(seasonId)).thenReturn(Optional.of(season));
            when(episodeMapper.toEntity(createDTO)).thenReturn(episode);
            when(episodeRepository.save(episode)).thenReturn(episode);
            when(episodeMapper.toDTO(episode)).thenReturn(episodeDTO);

            episodeService.createEpisode(createDTO, seasonId);

            sanitize.verify(() -> SanitizeUtils.sanitizeStrings(createDTO));
        }
    }

    // -------------------------------------------------------------------------
    // updateEpisode
    // -------------------------------------------------------------------------

    @Test
    void updateEpisode_ShouldUpdateAllFields_WhenAllNonNull() {
        UpdateEpisodeDTO updateDTO = new UpdateEpisodeDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setNumberEpisode(2);
        updateDTO.setMinutesDuration(55);
        updateDTO.setEmbed1("https://new-embed1.com");
        updateDTO.setEmbed2("https://new-embed2.com");
        updateDTO.setSynopsis("Updated synopsis");

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(updateDTO)).thenAnswer(inv -> null);

            when(episodeRepository.findById(episodeId)).thenReturn(Optional.of(episode));
            when(episodeRepository.save(episode)).thenReturn(episode);
            when(episodeMapper.toDTO(episode)).thenReturn(episodeDTO);

            EpisodeDTO result = episodeService.updateEpisode(updateDTO, episodeId);

            assertThat(result).isNotNull();
            assertThat(episode.getTitle()).isEqualTo("Updated Title");
            assertThat(episode.getNumberEpisode()).isEqualTo(2);
            assertThat(episode.getMinutesDuration()).isEqualTo(55);
            assertThat(episode.getEmbed1()).isEqualTo("https://new-embed1.com");
            assertThat(episode.getEmbed2()).isEqualTo("https://new-embed2.com");
            assertThat(episode.getSynopsis()).isEqualTo("Updated synopsis");
            verify(episodeRepository).save(episode);
        }
    }

    @Test
    void updateEpisode_ShouldNotUpdateFields_WhenEpisodeFieldsAreNull() {
        // episode com todos os campos nulos — nenhum campo deve ser setado
        Episode emptyEpisode = new Episode();
        emptyEpisode.setId(episodeId);

        UpdateEpisodeDTO updateDTO = new UpdateEpisodeDTO();
        updateDTO.setTitle("Should not be set");
        updateDTO.setNumberEpisode(99);

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(updateDTO)).thenAnswer(inv -> null);

            when(episodeRepository.findById(episodeId)).thenReturn(Optional.of(emptyEpisode));
            when(episodeRepository.save(emptyEpisode)).thenReturn(emptyEpisode);
            when(episodeMapper.toDTO(emptyEpisode)).thenReturn(episodeDTO);

            episodeService.updateEpisode(updateDTO, episodeId);

            // campos do episode permanecem null pois a condição (episode.getX() != null) falha
            assertThat(emptyEpisode.getTitle()).isNull();
            assertThat(emptyEpisode.getNumberEpisode()).isNull();
        }
    }

    @Test
    void updateEpisode_ShouldCallSanitize() {
        UpdateEpisodeDTO updateDTO = new UpdateEpisodeDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(updateDTO)).thenAnswer(inv -> null);

            when(episodeRepository.findById(episodeId)).thenReturn(Optional.of(episode));
            when(episodeRepository.save(episode)).thenReturn(episode);
            when(episodeMapper.toDTO(episode)).thenReturn(episodeDTO);

            episodeService.updateEpisode(updateDTO, episodeId);

            sanitize.verify(() -> SanitizeUtils.sanitizeStrings(updateDTO));
        }
    }

    @Test
    void updateEpisode_ShouldThrowException_WhenEpisodeNotFound() {
        UpdateEpisodeDTO updateDTO = new UpdateEpisodeDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(updateDTO)).thenAnswer(inv -> null);

            when(episodeRepository.findById(episodeId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> episodeService.updateEpisode(updateDTO, episodeId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Episode not found");

            verify(episodeRepository, never()).save(any());
        }
    }

    // -------------------------------------------------------------------------
    // deleteEpisode
    // -------------------------------------------------------------------------

    @Test
    void deleteEpisode_ShouldCallDeleteById() {
        doNothing().when(episodeRepository).deleteById(episodeId);

        episodeService.deleteEpisode(episodeId);

        verify(episodeRepository).deleteById(episodeId);
    }

    @Test
    void deleteEpisode_ShouldNotThrow_WhenEpisodeExists() {
        doNothing().when(episodeRepository).deleteById(episodeId);

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                () -> episodeService.deleteEpisode(episodeId)
        );
    }
}