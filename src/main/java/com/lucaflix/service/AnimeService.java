package com.lucaflix.service;

import com.lucaflix.dto.mapper.AnimeMapper;
import com.lucaflix.dto.response.anime.AnimeCompleteDTO;
import com.lucaflix.dto.request.anime.AnimeFilter;
import com.lucaflix.dto.response.anime.AnimeSimpleDTO;
import com.lucaflix.dto.response.page.PaginatedResponseDTO;
import com.lucaflix.model.*;
import com.lucaflix.model.enums.Categories;
import com.lucaflix.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnimeService {

    private final AnimeRepository animeRepository;
    private final LikeRepository likeRepository;
    private final MinhaListaRepository minhaListaRepository;
    private final UserRepository userRepository;
    private final AnimeMapper animeMapper;

    // Filtro de animes
    public PaginatedResponseDTO<AnimeSimpleDTO> filtrarAnime(AnimeFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Anime> animePage = animeRepository.buscarPorFiltros(
                filter.getTitle(),
                filter.getAvaliacao(),
                filter.getCategories(),
                pageable
        );
        return animeMapper.createPaginatedResponse(animePage);
    }

    // Top 10 mais curtidos
    public List<AnimeSimpleDTO> getTop10MostLiked() {
        List<Anime> topAnimes = animeRepository.findTop10ByLikes(PageRequest.of(0, 10));
        return topAnimes.stream()
                .map(animeMapper::convertToSimpleDTO)
                .collect(Collectors.toList());
    }

    public AnimeCompleteDTO getAnimeById(Long animeId, UUID userId) {
        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Anime não encontrado"
                ));
        return animeMapper.convertToCompleteDTO(anime, userId);
    }

    // Toggle Like - adiciona se não existir, remove se existir
    @Transactional
    public boolean toggleLike(UUID userId, Long animeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new RuntimeException("Anime não encontrado"));

        Like existingLike = likeRepository.findByUserAndAnime(user, anime).orElse(null);

        if (existingLike != null) {
            likeRepository.delete(existingLike);
            return false; // Removeu like
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setAnime(anime);
            likeRepository.save(like);
            return true; // Adicionou like
        }
    }

    // Toggle Minha Lista - adiciona se não existir, remove se existir
    @Transactional
    public boolean toggleMyList(UUID userId, Long animeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new RuntimeException("Anime não encontrado"));

        MyList existingItem = minhaListaRepository.findByUserAndAnime(user, anime).orElse(null);

        if (existingItem != null) {
            minhaListaRepository.delete(existingItem);
            return false; // Removeu da lista
        } else {
            MyList myList = new MyList();
            myList.setUser(user);
            myList.setAnime(anime);
            minhaListaRepository.save(myList);
            return true; // Adicionou à lista
        }
    }

    // Animes populares (mais curtidos)
    public PaginatedResponseDTO<AnimeSimpleDTO> getPopularAnimes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Anime> animePage = animeRepository.findPopularAnimes(pageable);
        return animeMapper.createPaginatedResponse(animePage);
    }

    // Novos lançamentos
    public PaginatedResponseDTO<AnimeSimpleDTO> getNewReleases(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Anime> animePage = animeRepository.findAll(pageable);
        return animeMapper.createPaginatedResponse(animePage);
    }

    // Animes por categoria
    public PaginatedResponseDTO<AnimeSimpleDTO> getAnimeByCategory(Categories categories, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Anime> animePage = animeRepository.findByCategoria(categories, pageable);
        return animeMapper.createPaginatedResponse(animePage);
    }

    // Animes com avaliação alta
    public PaginatedResponseDTO<AnimeSimpleDTO> getHighRatedAnimes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Anime> animePage = animeRepository.findByAvaliacaoGreaterThanEqual(7.0, pageable);
        return animeMapper.createPaginatedResponse(animePage);
    }

    // Recomendações
    public PaginatedResponseDTO<AnimeSimpleDTO> getRecommendations(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Anime> animePage = animeRepository.findRecommendations(userId, pageable);
        return animeMapper.createPaginatedResponse(animePage);
    }

    // Similar
    public PaginatedResponseDTO<AnimeSimpleDTO> getSimilarAnimes(Long animeId, int page, int size) {
        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new RuntimeException("Anime não encontrado"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Anime> animePage = animeRepository.findSimilarAnimes(anime.getCategories(), anime.getId(), pageable);
        return animeMapper.createPaginatedResponse(animePage);
    }

    // Animes por ano
    public PaginatedResponseDTO<AnimeSimpleDTO> getAnimesByYear(Integer year, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Anime> animePage = animeRepository.findByYear(year, pageable);
        return animeMapper.createPaginatedResponse(animePage);
    }
}