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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyListItemService {

    private final UserRepository userRepository;
    private final AnimeMapper animeMapper;
    private final MovieMapper movieMapper;
    private final SeriesMapper seriesMapper;
    private final PageMapper pageMapper;
    private final AnimeRepository animeRepository;
    private final MovieRepository movieRepository;
    private final SeriesRepository seriesRepository;
    private final MyListItemRepository myListItemRepository;


    public PaginatedResponseDTO<Object> getMyList(User userRequest, FilterDTO filter, int page, int size) {
        User user = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("addedAt").descending());
        Page<MyListItem> itemPage = myListItemRepository.findByUser(user, pageable);

        List<MyListItem> items = itemPage.getContent();
        List<UUID> movieIds = new ArrayList<>();
        List<UUID> seriesIds = new ArrayList<>();
        List<UUID> animeIds = new ArrayList<>();

        for (MyListItem item : items) {
            switch (item.getType()) {
                case MOVIE -> movieIds.add(item.getContentId());
                case SERIES -> seriesIds.add(item.getContentId());
                case ANIME -> animeIds.add(item.getContentId());
            }
        }
        Map<UUID, Movie> movies = movieRepository.findAllById(movieIds)
                .stream().collect(Collectors.toMap(Movie::getId, m -> m));
        Map<UUID, Series> series = seriesRepository.findAllById(seriesIds)
                .stream().collect(Collectors.toMap(Series::getId, s -> s));
        Map<UUID, Anime> animes = animeRepository.findAllById(animeIds)
                .stream().collect(Collectors.toMap(Anime::getId, a -> a));

        List<Object> content = items.stream()
                .map(item -> {
                    return switch (item.getType()) {
                        case MOVIE -> movieMapper.toSimple(movies.get(item.getContentId()), user);
                        case SERIES -> seriesMapper.toSimple(series.get(item.getContentId()), user);
                        case ANIME -> animeMapper.toSimple(animes.get(item.getContentId()), user);
                    };
                })
                .toList();
        return pageMapper.toPaginatedDTO(itemPage, content);
    }

    public void addMyList(UUID contentId, User userRequest, MediaType type) {
        User user = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (type == null) {
            throw new RuntimeException("Media type not found");
        }
        if (myListItemRepository.existsByUserAndContentIdAndType(user, contentId, type)) {
            return;
        }
        switch (type) {
            case MOVIE -> movieRepository.findById(contentId)
                    .orElseThrow(() -> new RuntimeException("Movie not found"));

            case SERIES -> seriesRepository.findById(contentId)
                    .orElseThrow(() -> new RuntimeException("Series not found"));

            case ANIME -> animeRepository.findById(contentId)
                    .orElseThrow(() -> new RuntimeException("Anime not found"));
        }
        MyListItem item = new MyListItem();
        item.setUser(user);
        item.setContentId(contentId);
        item.setType(type);
        item.setAddedAt(java.time.LocalDateTime.now());

        myListItemRepository.save(item);
    }

    public void removeMyList(UUID contentId, User userRequest, MediaType type) {
        User user = userRepository.findById(userRequest.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (type == null) {
            throw new RuntimeException("Media type not found");
        }
        MyListItem item = myListItemRepository.findByUserAndContentIdAndType(user, contentId, type)
                .orElseThrow(() -> new RuntimeException("Media not found in list"));

        myListItemRepository.delete(item);
    }
}