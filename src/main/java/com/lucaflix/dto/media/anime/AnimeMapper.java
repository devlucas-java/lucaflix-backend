package com.lucaflix.dto.media.anime;

import com.lucaflix.dto.media.PaginatedResponseDTO;
import com.lucaflix.model.Anime;
import com.lucaflix.model.User;
import com.lucaflix.repository.LikeRepository;
import com.lucaflix.repository.MinhaListaRepository;
import com.lucaflix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AnimeMapper {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final MinhaListaRepository minhaListaRepository;

    // Conversores
    public AnimeSimpleDTO convertToSimpleDTO(Anime anime) {
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

    public AnimeCompleteDTO convertToCompleteDTO(Anime anime, UUID userId) {
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

    public PaginatedResponseDTO<AnimeSimpleDTO> createPaginatedResponse(Page<Anime> animePage) {
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