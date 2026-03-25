package com.lucaflix.dto.mapper;

import com.lucaflix.dto.response.page.PaginatedResponseDTO;
import com.lucaflix.dto.response.anime.AnimeCompleteDTO;
import com.lucaflix.dto.response.anime.AnimeSimpleDTO;
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
        dto.setCategories(anime.getCategories());
        dto.setMinAge(anime.getMinAge());
        dto.setAvaliacao(anime.getAvaliacao());
        dto.setEmbed1(anime.getEmbed1());
        dto.setEmbed2(anime.getEmbed2());
        dto.setTrailer(anime.getTrailer());
        dto.setPosterURL1(anime.getPosterURL1());
        dto.setPosterURL2(anime.getPosterURL2());
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
        dto.setCategories(anime.getCategories());
        dto.setMinAge(anime.getMinAge());
        dto.setAvaliacao(anime.getAvaliacao());
        dto.setEmbed1(anime.getEmbed1());
        dto.setEmbed2(anime.getEmbed2());
        dto.setTrailer(anime.getTrailer());
        dto.setPosterURL1(anime.getPosterURL1());
        dto.setPosterURL2(anime.getPosterURL2());
        dto.setLogoURL1(anime.getLogoURL1());
        dto.setLogoURL2(anime.getLogoURL2());
        dto.setBackdropURL1(anime.getBackdropURL1());
        dto.setBackdropURL2(anime.getBackdropURL2());
        dto.setBackdropURL3(anime.getBackdropURL3());
        dto.setBackdropURL4(anime.getBackdropURL4());
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