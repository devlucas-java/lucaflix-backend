package com.lucaflix.service;

import com.lucaflix.dto.mapper.AnimeMapper;
import com.lucaflix.dto.mapper.PageMapper;
import com.lucaflix.dto.request.anime.CreateAnimeDTO;
import com.lucaflix.dto.request.anime.UpdateAnimeDTO;
import com.lucaflix.dto.request.others.FilterDTO;
import com.lucaflix.dto.response.anime.AnimeCompleteDTO;
import com.lucaflix.dto.response.anime.AnimeSimpleDTO;
import com.lucaflix.dto.response.others.PaginatedResponseDTO;
import com.lucaflix.model.Anime;
import com.lucaflix.model.User;
import com.lucaflix.repository.AnimeRepository;
import com.lucaflix.repository.UserRepository;
import com.lucaflix.service.utils.sanitize.SanitizeUtils;
import com.lucaflix.service.utils.validate.AnimeValidate;
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
public class AnimeServiceTest {

    @InjectMocks
    private AnimeService animeService;

    @Mock
    private AnimeRepository animeRepository;

    @Mock
    private AnimeMapper animeMapper;

    @Mock
    private AnimeValidate animeValidate;

    @Mock
    private PageMapper pageMapper;

    @Mock
    private UserRepository userRepository;

    private Anime anime;
    private User user;
    private UUID animeId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        animeId = UUID.randomUUID();
        userId = UUID.randomUUID();

        anime = new Anime();
        anime.setId(animeId);
        anime.setTitle("Attack on Titan");
        anime.setCategories(List.of());

        user = new User();
        user.setId(userId);
        user.setUsername("lucassilva");
    }

    // -------------------------------------------------------------------------
    // filterAnime
    // -------------------------------------------------------------------------

    @Test
    void filterAnime_ShouldReturnPaginatedResponse() {
        FilterDTO filter = new FilterDTO();
        Page<Anime> animePage = new PageImpl<>(List.of(anime));
        PaginatedResponseDTO<AnimeSimpleDTO> expected = new PaginatedResponseDTO<>();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(filter)).thenAnswer(inv -> null);

            when(animeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(animePage);
            when(pageMapper.toPaginatedDTO(eq(animePage), any(Function.class))).thenReturn(expected);

            PaginatedResponseDTO<AnimeSimpleDTO> result = animeService.filterAnime(filter, 0, 20);

            assertThat(result).isNotNull();
            verify(animeRepository).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    @Test
    void filterAnime_ShouldNormalizePage_WhenPageIsNegative() {
        FilterDTO filter = new FilterDTO();
        Page<Anime> animePage = new PageImpl<>(List.of());
        PaginatedResponseDTO<AnimeSimpleDTO> expected = new PaginatedResponseDTO<>();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(filter)).thenAnswer(inv -> null);

            when(animeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(animePage);
            when(pageMapper.toPaginatedDTO(eq(animePage), any(Function.class))).thenReturn(expected);

            assertThat(animeService.filterAnime(filter, -5, 20)).isNotNull();
        }
    }

    @Test
    void filterAnime_ShouldNormalizeSize_WhenSizeIsInvalid() {
        FilterDTO filter = new FilterDTO();
        Page<Anime> animePage = new PageImpl<>(List.of());
        PaginatedResponseDTO<AnimeSimpleDTO> expected = new PaginatedResponseDTO<>();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(filter)).thenAnswer(inv -> null);

            when(animeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(animePage);
            when(pageMapper.toPaginatedDTO(eq(animePage), any(Function.class))).thenReturn(expected);

            assertThat(animeService.filterAnime(filter, 0, 0)).isNotNull();
            assertThat(animeService.filterAnime(filter, 0, 999)).isNotNull();
        }
    }

    @Test
    void filterAnime_ShouldCallSanitize() {
        FilterDTO filter = new FilterDTO();
        Page<Anime> animePage = new PageImpl<>(List.of());

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(filter)).thenAnswer(inv -> null);

            when(animeRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(animePage);
            when(pageMapper.toPaginatedDTO(eq(animePage), any(Function.class))).thenReturn(new PaginatedResponseDTO<>());

            animeService.filterAnime(filter, 0, 20);

            sanitize.verify(() -> SanitizeUtils.sanitizeStrings(filter));
        }
    }

    // -------------------------------------------------------------------------
    // getAnimeById
    // -------------------------------------------------------------------------

    @Test
    void getAnimeById_ShouldReturnCompleteDTO_WhenUserIsNull() {
        AnimeCompleteDTO expected = new AnimeCompleteDTO();
        when(animeRepository.findById(animeId)).thenReturn(Optional.of(anime));
        when(animeMapper.toComplete(anime, null)).thenReturn(expected);

        AnimeCompleteDTO result = animeService.getAnimeById(animeId, null);

        assertThat(result).isNotNull();
        verify(userRepository, never()).findById(any());
    }

    @Test
    void getAnimeById_ShouldReturnCompleteDTO_WhenUserIsProvided() {
        AnimeCompleteDTO expected = new AnimeCompleteDTO();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(animeRepository.findById(animeId)).thenReturn(Optional.of(anime));
        when(animeMapper.toComplete(anime, user)).thenReturn(expected);

        AnimeCompleteDTO result = animeService.getAnimeById(animeId, user);

        assertThat(result).isNotNull();
        verify(userRepository).findById(userId);
    }

    @Test
    void getAnimeById_ShouldThrowException_WhenAnimeNotFound() {
        when(animeRepository.findById(animeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> animeService.getAnimeById(animeId, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Anime not found");
    }

    @Test
    void getAnimeById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> animeService.getAnimeById(animeId, user))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    // -------------------------------------------------------------------------
    // getSimilarAnime
    // -------------------------------------------------------------------------

    @Test
    void getSimilarAnime_ShouldReturnPaginatedSimilarAnimes() {
        Page<Anime> similarPage = new PageImpl<>(List.of(anime));
        PaginatedResponseDTO<AnimeSimpleDTO> expected = new PaginatedResponseDTO<>();

        when(animeRepository.findById(animeId)).thenReturn(Optional.of(anime));
        when(animeRepository.findSimilarAnime(any(), eq(animeId), any(Pageable.class))).thenReturn(similarPage);
        when(pageMapper.toPaginatedDTO(eq(similarPage), any(Function.class))).thenReturn(expected);

        PaginatedResponseDTO<AnimeSimpleDTO> result = animeService.getSimilarAnime(animeId, 0, 10);

        assertThat(result).isNotNull();
        verify(animeRepository).findSimilarAnime(any(), eq(animeId), any(Pageable.class));
    }

    @Test
    void getSimilarAnime_ShouldThrowException_WhenAnimeNotFound() {
        when(animeRepository.findById(animeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> animeService.getSimilarAnime(animeId, 0, 10))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Anime not found");
    }

    // -------------------------------------------------------------------------
    // updateAnime
    // -------------------------------------------------------------------------

    @Test
    void updateAnime_ShouldUpdateAndReturnCompleteDTO() {
        UpdateAnimeDTO updateDTO = new UpdateAnimeDTO();
        AnimeCompleteDTO expected = new AnimeCompleteDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(updateDTO)).thenAnswer(inv -> null);

            when(animeRepository.findById(animeId)).thenReturn(Optional.of(anime));
            when(animeValidate.validUpdate(updateDTO, anime)).thenReturn(null);
            when(animeRepository.save(anime)).thenReturn(anime);
            when(animeMapper.toComplete(anime, null)).thenReturn(expected);

            AnimeCompleteDTO result = animeService.updateAnime(updateDTO, animeId);

            assertThat(result).isNotNull();
            verify(animeValidate).validUpdate(updateDTO, anime);
            verify(animeRepository).save(anime);
        }
    }

    @Test
    void updateAnime_ShouldThrowException_WhenAnimeNotFound() {
        UpdateAnimeDTO updateDTO = new UpdateAnimeDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            when(animeRepository.findById(animeId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> animeService.updateAnime(updateDTO, animeId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Anime not found");

            verify(animeValidate, never()).validUpdate(any(), any());
            verify(animeRepository, never()).save(any());
        }
    }

    @Test
    void updateAnime_ShouldCallSanitize() {
        UpdateAnimeDTO updateDTO = new UpdateAnimeDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(updateDTO)).thenAnswer(inv -> null);

            when(animeRepository.findById(animeId)).thenReturn(Optional.of(anime));
            when(animeValidate.validUpdate(updateDTO, anime)).thenReturn(null);
            when(animeRepository.save(anime)).thenReturn(anime);
            when(animeMapper.toComplete(anime, null)).thenReturn(new AnimeCompleteDTO());

            animeService.updateAnime(updateDTO, animeId);

            sanitize.verify(() -> SanitizeUtils.sanitizeStrings(updateDTO));
        }
    }

    // -------------------------------------------------------------------------
    // createAnime
    // -------------------------------------------------------------------------

    @Test
    void createAnime_ShouldSaveAndReturnCompleteDTO() {
        CreateAnimeDTO createDTO = new CreateAnimeDTO();
        AnimeCompleteDTO expected = new AnimeCompleteDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(createDTO)).thenAnswer(inv -> null);

            when(animeMapper.toEntity(createDTO)).thenReturn(anime);
            when(animeRepository.save(anime)).thenReturn(anime);
            when(animeMapper.toComplete(anime, null)).thenReturn(expected);

            AnimeCompleteDTO result = animeService.createAnime(createDTO);

            assertThat(result).isNotNull();
            verify(animeRepository).save(anime);
        }
    }

    @Test
    void createAnime_ShouldCallSanitize() {
        CreateAnimeDTO createDTO = new CreateAnimeDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(createDTO)).thenAnswer(inv -> null);

            when(animeMapper.toEntity(createDTO)).thenReturn(anime);
            when(animeRepository.save(anime)).thenReturn(anime);
            when(animeMapper.toComplete(anime, null)).thenReturn(new AnimeCompleteDTO());

            animeService.createAnime(createDTO);

            sanitize.verify(() -> SanitizeUtils.sanitizeStrings(createDTO));
        }
    }

    @Test
    void deleteAnime_ShouldDeleteAnime_WhenFound() {
        when(animeRepository.findById(animeId)).thenReturn(Optional.of(anime));

        animeService.deleteAnime(animeId);

        verify(animeRepository).delete(anime);
    }
}