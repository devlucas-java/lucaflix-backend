package com.lucaflix.service;

import com.lucaflix.dto.mapper.AnimeMapper;
import com.lucaflix.dto.mapper.PageMapper;
import com.lucaflix.dto.request.anime.CreateAnimeDTO;
import com.lucaflix.dto.request.anime.UpdateAnimeDTO;
import com.lucaflix.dto.request.others.FilterDTO;
import com.lucaflix.dto.response.anime.AnimeCompleteDTO;
import com.lucaflix.dto.response.anime.AnimeSimpleDTO;
import com.lucaflix.dto.response.others.PaginatedResponseDTO;
import com.lucaflix.exception.ResourceNotFoundException;
import com.lucaflix.model.Anime;
import com.lucaflix.model.User;
import com.lucaflix.repository.AnimeRepository;
import com.lucaflix.repository.UserRepository;
import com.lucaflix.service.utils.sanitize.SanitizeUtils;
import com.lucaflix.service.utils.spec.AnimeSpecification;
import com.lucaflix.service.utils.validate.AnimeValidate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnimeService {

    private final AnimeRepository animeRepository;
    private final AnimeMapper animeMapper;
    private final AnimeValidate animeValidate;
    private final PageMapper pageMapper;
    private final UserRepository userRepository;

    public PaginatedResponseDTO<AnimeSimpleDTO> filterAnime(FilterDTO filter, int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20;

        SanitizeUtils.sanitizeStrings(filter);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "dateRegistered")
        );

        AnimeSpecification spec = new AnimeSpecification(filter);
        Page<Anime> animePage = animeRepository.findAll(spec, pageable);

        return pageMapper.toPaginatedDTO(animePage, a -> animeMapper.toSimple(a, null));
    }

    public AnimeCompleteDTO getAnimeById(UUID id, User userRequest) {
        User user = null;

        if (userRequest != null) {
            user = userRepository.findById(userRequest.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }

        Anime anime = findAnimeOrThrow(id);

        return animeMapper.toComplete(anime, user);
    }

    public PaginatedResponseDTO<AnimeSimpleDTO> getSimilarAnime(UUID animeId, int page, int size) {
        Anime anime = findAnimeOrThrow(animeId);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "rating")
        );

        Page<Anime> animePage = animeRepository.findSimilarAnime(
                anime.getCategories(),
                anime.getId(),
                pageable
        );

        return pageMapper.toPaginatedDTO(animePage, a -> animeMapper.toSimple(a, null));
    }

    public AnimeCompleteDTO updateAnime(UpdateAnimeDTO updateDTO, UUID id) {
        Anime anime = findAnimeOrThrow(id);

        SanitizeUtils.sanitizeStrings(updateDTO);
        animeValidate.validUpdate(updateDTO, anime);

        Anime saved = animeRepository.save(anime);

        return animeMapper.toComplete(saved, null);
    }

    public AnimeCompleteDTO createAnime(CreateAnimeDTO createDTO) {
        SanitizeUtils.sanitizeStrings(createDTO);

        Anime anime = animeMapper.toEntity(createDTO);
        Anime saved = animeRepository.save(anime);

        return animeMapper.toComplete(saved, null);
    }

    public void deleteAnime(UUID id) {
        Anime anime = findAnimeOrThrow(id);
        animeRepository.delete(anime);
    }

    private Anime findAnimeOrThrow(UUID id) {
        return animeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Anime not found"));
    }
}