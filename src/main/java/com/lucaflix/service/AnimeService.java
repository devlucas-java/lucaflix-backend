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
import com.lucaflix.repository.LikeRepository;
import com.lucaflix.repository.MyListItemRepository;
import com.lucaflix.repository.UserRepository;
import com.lucaflix.service.utils.spec.AnimeSpecification;
import com.lucaflix.service.utils.validate.AnimeValidate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnimeService {

    private final AnimeRepository animeRepository;
    private final AnimeMapper animeMapper;
    private final AnimeValidate animeValidate;
    private final PageMapper pageMapper;
    private final LikeRepository likeRepository;
    private final MyListItemRepository myListItemRepository;
    private final UserRepository userRepository;


    public PaginatedResponseDTO<AnimeSimpleDTO> filterAnime(FilterDTO filter, int page, int size) {

        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20;

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "dateRegistered"));

        AnimeSpecification spec = new AnimeSpecification(filter);
        Page<Anime> animePage = animeRepository.findAll(spec, pageable);

        return pageMapper.toPaginatedDTO(animePage, a -> animeMapper.toSimple(a, null));
    }

    public AnimeCompleteDTO getAnimeById(UUID id, User userRequest) {
        User user = null;
        if (userRequest != null) {
            user = userRepository.findById(userRequest.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        Anime anime = animeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anime not found"));

        return animeMapper.toComplete(anime, user);
    }

    public PaginatedResponseDTO<AnimeSimpleDTO> getSimilarAnimes(UUID animeId, int page, int size) {
        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new RuntimeException("Anime not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rating"));
        Page<Anime> animePage = animeRepository.findSimilarAnimes(
                anime.getCategories(),
                anime.getId(),
                pageable);

        return pageMapper.toPaginatedDTO(animePage, a -> animeMapper.toSimple(a, null));
    }

    public AnimeCompleteDTO updateAnime(UpdateAnimeDTO updateDTO, UUID id){
        Anime anime = animeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anime not found"));

        animeValidate.validUpdate(updateDTO, anime);
        animeRepository.save(anime);
        return animeMapper.toComplete(anime, null);
    }

    public AnimeCompleteDTO createAnime(CreateAnimeDTO createDTO) {

        Anime anime = animeMapper.toEntity(createDTO);
        Anime saved = animeRepository.save(anime);

        return animeMapper.toComplete(saved, null);
    }

    public void deleteAnime(UUID id) {
        Anime anime = animeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anime not found"));

        likeRepository.deleteByAnime(anime);
        myListItemRepository.deleteByAnime(anime);

        animeRepository.deleteById(id);
    }
}