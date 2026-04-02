package com.lucaflix.service;

import com.lucaflix.dto.mapper.AnimeMapper;
import com.lucaflix.dto.mapper.MovieMapper;
import com.lucaflix.dto.mapper.PageMapper;
import com.lucaflix.dto.mapper.SeriesMapper;
import com.lucaflix.dto.request.others.FilterDTO;
import com.lucaflix.dto.response.others.PaginatedResponseDTO;
import com.lucaflix.model.*;
import com.lucaflix.model.enums.MediaType;
import com.lucaflix.repository.*;
import com.lucaflix.service.utils.sanitize.SanitizeUtils;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MyListItemServiceTest {

    @InjectMocks
    private MyListItemService myListItemService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AnimeMapper animeMapper;

    @Mock
    private MovieMapper movieMapper;

    @Mock
    private SeriesMapper seriesMapper;

    @Mock
    private PageMapper pageMapper;

    @Mock
    private AnimeRepository animeRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private MyListItemRepository myListItemRepository;

    private User user;
    private UUID userId;
    private UUID contentId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        contentId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setUsername("lucassilva");


    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private MyListItem buildItem(MediaType type) {
        MyListItem item = new MyListItem();
        item.setUser(user);
        item.setContentId(contentId);
        item.setType(type);
        item.setAddedAt(LocalDateTime.now());
        return item;
    }

    // -------------------------------------------------------------------------
    // getMyList
    // -------------------------------------------------------------------------

    @Test
    void getMyList_ShouldReturnPaginatedResponse_WhenListIsEmpty() {
        FilterDTO filter = new FilterDTO();
        Page<MyListItem> emptyPage = new PageImpl<>(List.of());
        PaginatedResponseDTO<Object> expected = new PaginatedResponseDTO<>();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(filter)).thenAnswer(inv -> null);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(myListItemRepository.findByUser(eq(user), any(Pageable.class))).thenReturn(emptyPage);
            when(movieRepository.findAllById(any())).thenReturn(List.of());
            when(seriesRepository.findAllById(any())).thenReturn(List.of());
            when(animeRepository.findAllById(any())).thenReturn(List.of());
            when(pageMapper.toPaginatedDTO(eq(emptyPage), any(List.class))).thenReturn(expected);

            PaginatedResponseDTO<Object> result = myListItemService.getMyList(user, filter, 0, 10);

            assertThat(result).isNotNull();
            verify(myListItemRepository).findByUser(eq(user), any(Pageable.class));
        }
    }

    @Test
    void getMyList_ShouldMapMovieItem_WhenTypeIsMovie() {
        FilterDTO filter = new FilterDTO();
        MyListItem movieItem = buildItem(MediaType.MOVIE);
        Page<MyListItem> page = new PageImpl<>(List.of(movieItem));

        Movie movie = new Movie();
        movie.setId(contentId);

        PaginatedResponseDTO<Object> expected = new PaginatedResponseDTO<>();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(filter)).thenAnswer(inv -> null);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(myListItemRepository.findByUser(eq(user), any(Pageable.class))).thenReturn(page);
            when(movieRepository.findAllById(List.of(contentId))).thenReturn(List.of(movie));
            when(seriesRepository.findAllById(any())).thenReturn(List.of());
            when(animeRepository.findAllById(any())).thenReturn(List.of());
            when(pageMapper.toPaginatedDTO(eq(page), any(List.class))).thenReturn(expected);

            PaginatedResponseDTO<Object> result = myListItemService.getMyList(user, filter, 0, 10);

            assertThat(result).isNotNull();
            verify(movieMapper).toSimple(movie, user);
            verify(seriesMapper, never()).toSimple(any(), any());
            verify(animeMapper, never()).toSimple(any(), any());
        }
    }

    @Test
    void getMyList_ShouldMapSeriesItem_WhenTypeIsSeries() {
        FilterDTO filter = new FilterDTO();
        MyListItem seriesItem = buildItem(MediaType.SERIES);
        Page<MyListItem> page = new PageImpl<>(List.of(seriesItem));

        Series seriesObj = new Series();
        seriesObj.setId(contentId);

        PaginatedResponseDTO<Object> expected = new PaginatedResponseDTO<>();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(filter)).thenAnswer(inv -> null);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(myListItemRepository.findByUser(eq(user), any(Pageable.class))).thenReturn(page);
            when(movieRepository.findAllById(any())).thenReturn(List.of());
            when(seriesRepository.findAllById(List.of(contentId))).thenReturn(List.of(seriesObj));
            when(animeRepository.findAllById(any())).thenReturn(List.of());
            when(pageMapper.toPaginatedDTO(eq(page), any(List.class))).thenReturn(expected);

            PaginatedResponseDTO<Object> result = myListItemService.getMyList(user, filter, 0, 10);

            assertThat(result).isNotNull();
            verify(seriesMapper).toSimple(seriesObj, user);
            verify(movieMapper, never()).toSimple(any(), any());
            verify(animeMapper, never()).toSimple(any(), any());
        }
    }

    @Test
    void getMyList_ShouldMapAnimeItem_WhenTypeIsAnime() {
        FilterDTO filter = new FilterDTO();
        MyListItem animeItem = buildItem(MediaType.ANIME);
        Page<MyListItem> page = new PageImpl<>(List.of(animeItem));

        Anime anime = new Anime();
        anime.setId(contentId);

        PaginatedResponseDTO<Object> expected = new PaginatedResponseDTO<>();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(filter)).thenAnswer(inv -> null);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(myListItemRepository.findByUser(eq(user), any(Pageable.class))).thenReturn(page);
            when(movieRepository.findAllById(any())).thenReturn(List.of());
            when(seriesRepository.findAllById(any())).thenReturn(List.of());
            when(animeRepository.findAllById(List.of(contentId))).thenReturn(List.of(anime));
            when(pageMapper.toPaginatedDTO(eq(page), any(List.class))).thenReturn(expected);

            PaginatedResponseDTO<Object> result = myListItemService.getMyList(user, filter, 0, 10);

            assertThat(result).isNotNull();
            verify(animeMapper).toSimple(anime, user);
            verify(movieMapper, never()).toSimple(any(), any());
            verify(seriesMapper, never()).toSimple(any(), any());
        }
    }

    @Test
    void getMyList_ShouldThrowException_WhenUserNotFound() {
        FilterDTO filter = new FilterDTO();

        try (MockedStatic<SanitizeUtils> sanitize = mockStatic(SanitizeUtils.class)) {
            sanitize.when(() -> SanitizeUtils.sanitizeStrings(filter)).thenAnswer(inv -> null);

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> myListItemService.getMyList(user, filter, 0, 10))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("User not found");

            verify(myListItemRepository, never()).findByUser(any(), any());
        }
    }

    // -------------------------------------------------------------------------
    // addMyList
    // -------------------------------------------------------------------------

    @Test
    void addMyList_ShouldSaveItem_WhenMovieExistsAndNotInList() {
        Movie movie = new Movie();
        movie.setId(contentId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(myListItemRepository.existsByUserAndContentIdAndType(user, contentId, MediaType.MOVIE)).thenReturn(false);
        when(movieRepository.findById(contentId)).thenReturn(Optional.of(movie));

        myListItemService.addMyList(contentId, user, MediaType.MOVIE);

        verify(myListItemRepository).save(any(MyListItem.class));
    }

    @Test
    void addMyList_ShouldSaveItem_WhenSeriesExistsAndNotInList() {
        Series seriesObj = new Series();
        seriesObj.setId(contentId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(myListItemRepository.existsByUserAndContentIdAndType(user, contentId, MediaType.SERIES)).thenReturn(false);
        when(seriesRepository.findById(contentId)).thenReturn(Optional.of(seriesObj));

        myListItemService.addMyList(contentId, user, MediaType.SERIES);

        verify(myListItemRepository).save(any(MyListItem.class));
    }

    @Test
    void addMyList_ShouldSaveItem_WhenAnimeExistsAndNotInList() {
        Anime anime = new Anime();
        anime.setId(contentId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(myListItemRepository.existsByUserAndContentIdAndType(user, contentId, MediaType.ANIME)).thenReturn(false);
        when(animeRepository.findById(contentId)).thenReturn(Optional.of(anime));

        myListItemService.addMyList(contentId, user, MediaType.ANIME);

        verify(myListItemRepository).save(any(MyListItem.class));
    }

    @Test
    void addMyList_ShouldNotSave_WhenItemAlreadyInList() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(myListItemRepository.existsByUserAndContentIdAndType(user, contentId, MediaType.MOVIE)).thenReturn(true);

        myListItemService.addMyList(contentId, user, MediaType.MOVIE);

        verify(myListItemRepository, never()).save(any());
    }

    @Test
    void addMyList_ShouldThrowException_WhenTypeIsNull() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> myListItemService.addMyList(contentId, user, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Media type not found");

        verify(myListItemRepository, never()).save(any());
    }

    @Test
    void addMyList_ShouldThrowException_WhenMovieNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(myListItemRepository.existsByUserAndContentIdAndType(user, contentId, MediaType.MOVIE)).thenReturn(false);
        when(movieRepository.findById(contentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> myListItemService.addMyList(contentId, user, MediaType.MOVIE))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Movie not found");

        verify(myListItemRepository, never()).save(any());
    }

    @Test
    void addMyList_ShouldThrowException_WhenSeriesNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(myListItemRepository.existsByUserAndContentIdAndType(user, contentId, MediaType.SERIES)).thenReturn(false);
        when(seriesRepository.findById(contentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> myListItemService.addMyList(contentId, user, MediaType.SERIES))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Series not found");

        verify(myListItemRepository, never()).save(any());
    }

    @Test
    void addMyList_ShouldThrowException_WhenAnimeNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(myListItemRepository.existsByUserAndContentIdAndType(user, contentId, MediaType.ANIME)).thenReturn(false);
        when(animeRepository.findById(contentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> myListItemService.addMyList(contentId, user, MediaType.ANIME))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Anime not found");

        verify(myListItemRepository, never()).save(any());
    }

    @Test
    void addMyList_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> myListItemService.addMyList(contentId, user, MediaType.MOVIE))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");

        verify(myListItemRepository, never()).save(any());
    }

    @Test
    void addMyList_ShouldSetCorrectFieldsOnSavedItem() {
        Movie movie = new Movie();
        movie.setId(contentId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(myListItemRepository.existsByUserAndContentIdAndType(user, contentId, MediaType.MOVIE)).thenReturn(false);
        when(movieRepository.findById(contentId)).thenReturn(Optional.of(movie));

        myListItemService.addMyList(contentId, user, MediaType.MOVIE);

        verify(myListItemRepository).save(argThat(item ->
                item.getUser().equals(user) &&
                        item.getContentId().equals(contentId) &&
                        item.getType() == MediaType.MOVIE &&
                        item.getAddedAt() != null
        ));
    }

    // -------------------------------------------------------------------------
    // removeMyList
    // -------------------------------------------------------------------------

    @Test
    void removeMyList_ShouldDeleteItem_WhenFound() {
        MyListItem item = buildItem(MediaType.MOVIE);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(myListItemRepository.findByUserAndContentIdAndType(user, contentId, MediaType.MOVIE))
                .thenReturn(Optional.of(item));

        myListItemService.removeMyList(contentId, user, MediaType.MOVIE);

        verify(myListItemRepository).delete(item);
    }

    @Test
    void removeMyList_ShouldThrowException_WhenTypeIsNull() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> myListItemService.removeMyList(contentId, user, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Media type not found");

        verify(myListItemRepository, never()).delete(any());
    }

    @Test
    void removeMyList_ShouldThrowException_WhenItemNotInList() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(myListItemRepository.findByUserAndContentIdAndType(user, contentId, MediaType.SERIES))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> myListItemService.removeMyList(contentId, user, MediaType.SERIES))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Media not found in list");

        verify(myListItemRepository, never()).delete(any());
    }

    @Test
    void removeMyList_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> myListItemService.removeMyList(contentId, user, MediaType.MOVIE))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");

        verify(myListItemRepository, never()).delete(any());
    }
}