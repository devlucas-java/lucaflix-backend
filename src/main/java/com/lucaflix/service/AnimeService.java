package com.lucaflix.service;

import com.lucaflix.dto.media.AnimeCompleteDTO;
import com.lucaflix.dto.media.AnimeFilter;
import com.lucaflix.dto.media.AnimeSimpleDTO;
import com.lucaflix.dto.media.PaginatedResponseDTO;
import com.lucaflix.model.*;
import com.lucaflix.model.enums.Categoria;
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

    // Filtro de animes
    public PaginatedResponseDTO<AnimeSimpleDTO> filtrarAnime(AnimeFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Anime> animePage = animeRepository.buscarPorFiltros(
                filter.getTitle(),
                filter.getAvaliacao(),
                filter.getCategoria(),
                pageable
        );
        return createPaginatedResponse(animePage);
    }

    // Top 10 mais curtidos
    public List<AnimeSimpleDTO> getTop10MostLiked() {
        List<Anime> topAnimes = animeRepository.findTop10ByLikes(PageRequest.of(0, 10));
        return topAnimes.stream().map(this::convertToSimpleDTO).collect(Collectors.toList());
    }

    public AnimeCompleteDTO getAnimeById(Long animeId, UUID userId) {
        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Anime não encontrado"
                ));
        return convertToCompleteDTO(anime, userId);
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

        MinhaLista existingItem = minhaListaRepository.findByUserAndAnime(user, anime).orElse(null);

        if (existingItem != null) {
            minhaListaRepository.delete(existingItem);
            return false; // Removeu da lista
        } else {
            MinhaLista minhaLista = new MinhaLista();
            minhaLista.setUser(user);
            minhaLista.setAnime(anime);
            minhaListaRepository.save(minhaLista);
            return true; // Adicionou à lista
        }
    }

    // Minha lista do usuário
    public PaginatedResponseDTO<AnimeSimpleDTO> getMyList(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("dataAdicao").descending());
        Page<MinhaLista> myListPage = minhaListaRepository.findAnimesByUser(user, pageable);

        List<AnimeSimpleDTO> animeList = myListPage.getContent().stream()
                .filter(item -> item.getAnime() != null)
                .map(item -> convertToSimpleDTO(item.getAnime()))
                .collect(Collectors.toList());

        return new PaginatedResponseDTO<>(
                animeList,
                myListPage.getNumber(),
                myListPage.getTotalPages(),
                myListPage.getTotalElements(),
                myListPage.getSize(),
                myListPage.isFirst(),
                myListPage.isLast(),
                myListPage.hasNext(),
                myListPage.hasPrevious()
        );
    }

    // Animes populares (mais curtidos)
    public PaginatedResponseDTO<AnimeSimpleDTO> getPopularAnimes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Anime> animePage = animeRepository.findPopularAnimes(pageable);
        return createPaginatedResponse(animePage);
    }

    // Novos lançamentos
    public PaginatedResponseDTO<AnimeSimpleDTO> getNewReleases(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Anime> animePage = animeRepository.findAll(pageable);
        return createPaginatedResponse(animePage);
    }

    // Animes por categoria
    public PaginatedResponseDTO<AnimeSimpleDTO> getAnimeByCategory(Categoria categoria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Anime> animePage = animeRepository.findByCategoria(categoria, pageable);
        return createPaginatedResponse(animePage);
    }

    // Animes com avaliação alta
    public PaginatedResponseDTO<AnimeSimpleDTO> getHighRatedAnimes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Anime> animePage = animeRepository.findByAvaliacaoGreaterThanEqual(7.0, pageable);
        return createPaginatedResponse(animePage);
    }

    // Recomendações
    public PaginatedResponseDTO<AnimeSimpleDTO> getRecommendations(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Anime> animePage = animeRepository.findRecommendations(userId, pageable);
        return createPaginatedResponse(animePage);
    }

    // Similar
    public PaginatedResponseDTO<AnimeSimpleDTO> getSimilarAnimes(Long animeId, int page, int size) {
        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new RuntimeException("Anime não encontrado"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "avaliacao"));
        Page<Anime> animePage = animeRepository.findSimilarAnimes(anime.getCategoria(), anime.getId(), pageable);
        return createPaginatedResponse(animePage);
    }

    // Animes por ano
    public PaginatedResponseDTO<AnimeSimpleDTO> getAnimesByYear(Integer year, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCadastro"));
        Page<Anime> animePage = animeRepository.findByYear(year, pageable);
        return createPaginatedResponse(animePage);
    }

    // Conversores
    private AnimeSimpleDTO convertToSimpleDTO(Anime anime) {
        AnimeSimpleDTO dto = new AnimeSimpleDTO();
        dto.setId(anime.getId());
        dto.setTitle(anime.getTitle());
        dto.setAnoLancamento(anime.getAnoLancamento());
        dto.setTmdbId(anime.getTmdbId());
        dto.setImdbId(anime.getImdbId());
        dto.setPaisOrigen(anime.getPaisOrigen());
        dto.setCategoria(anime.getCategoria());
        dto.setMinAge(anime.getMinAge());
        dto.setAvaliacao(anime.getAvaliacao());
        dto.setEmbed1(anime.getEmbed1());
        dto.setEmbed2(anime.getEmbed2());
        dto.setTrailer(anime.getTrailer());
        dto.setImageURL1(anime.getImageURL1());
        dto.setImageURL2(anime.getImageURL2());
        dto.setTotalTemporadas(anime.getTotalTemporadas());
        dto.setTotalEpisodios(anime.getTotalEpisodios());
        dto.setTotalLikes((long) (anime.getLikes() != null ? anime.getLikes().size() : 0));
        return dto;
    }

    private AnimeCompleteDTO convertToCompleteDTO(Anime anime, UUID userId) {
        AnimeCompleteDTO dto = new AnimeCompleteDTO();
        dto.setId(anime.getId());
        dto.setTitle(anime.getTitle());
        dto.setAnoLancamento(anime.getAnoLancamento());
        dto.setTmdbId(anime.getTmdbId());
        dto.setImdbId(anime.getImdbId());
        dto.setPaisOrigen(anime.getPaisOrigen());
        dto.setSinopse(anime.getSinopse());
        dto.setDataCadastro(anime.getDataCadastro());
        dto.setCategoria(anime.getCategoria());
        dto.setMinAge(anime.getMinAge());
        dto.setAvaliacao(anime.getAvaliacao());
        dto.setEmbed1(anime.getEmbed1());
        dto.setEmbed2(anime.getEmbed2());
        dto.setTrailer(anime.getTrailer());
        dto.setImageURL1(anime.getImageURL1());
        dto.setImageURL2(anime.getImageURL2());
        dto.setTotalTemporadas(anime.getTotalTemporadas());
        dto.setTotalEpisodios(anime.getTotalEpisodios());
        dto.setTotalLikes((long) (anime.getLikes() != null ? anime.getLikes().size() : 0));

        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                dto.setUserLiked(likeRepository.existsByUserAndAnime(user, anime));
                dto.setInUserList(minhaListaRepository.existsByUserAndAnime(user, anime));
            }
        } else {
            dto.setInUserList(null);
            dto.setUserLiked(null);
        }
        return dto;
    }

    private PaginatedResponseDTO<AnimeSimpleDTO> createPaginatedResponse(Page<Anime> animePage) {
        List<AnimeSimpleDTO> animeDTOs = animePage.getContent().stream()
                .map(this::convertToSimpleDTO)
                .collect(Collectors.toList());

        return new PaginatedResponseDTO<>(
                animeDTOs,
                animePage.getNumber(),
                animePage.getTotalPages(),
                animePage.getTotalElements(),
                animePage.getSize(),
                animePage.isFirst(),
                animePage.isLast(),
                animePage.hasNext(),
                animePage.hasPrevious()
        );
    }
}