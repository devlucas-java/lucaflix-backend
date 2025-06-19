package com.lucaflix.dto.media.movie;

import com.lucaflix.dto.media.PaginatedResponseDTO;
import com.lucaflix.model.Movie;
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
public class MovieMapper {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final MinhaListaRepository minhaListaRepository;

    // Conversores
    public MovieSimpleDTO convertToSimpleDTO(Movie movie) {
        MovieSimpleDTO dto = new MovieSimpleDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setAnoLancamento(movie.getAnoLancamento());
        dto.setDuracaoMinutos(movie.getDuracaoMinutos());
        dto.setTmdbId(movie.getTmdbId());
        dto.setImdbId(movie.getImdbId());
        dto.setPaisOrigen(movie.getPaisOrigen());
        dto.setCategoria(movie.getCategoria());
        dto.setMinAge(movie.getMinAge());
        dto.setAvaliacao(movie.getAvaliacao());
        dto.setImageURL1(movie.getImageURL1());
        dto.setImageURL2(movie.getImageURL2());
        dto.setTotalLikes((long) (movie.getLikes() != null ? movie.getLikes().size() : 0));
        return dto;
    }

    public MovieCompleteDTO convertToCompleteDTO(Movie movie, UUID userId) {
        MovieCompleteDTO dto = new MovieCompleteDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setAnoLancamento(movie.getAnoLancamento());
        dto.setDuracaoMinutos(movie.getDuracaoMinutos());
        dto.setTmdbId(movie.getTmdbId());
        dto.setImdbId(movie.getImdbId());
        dto.setPaisOrigen(movie.getPaisOrigen());
        dto.setSinopse(movie.getSinopse());
        dto.setDataCadastro(movie.getDataCadastro());
        dto.setCategoria(movie.getCategoria());
        dto.setMinAge(movie.getMinAge());
        dto.setAvaliacao(movie.getAvaliacao());
        dto.setEmbed1(movie.getEmbed1());
        dto.setEmbed2(movie.getEmbed2());
        dto.setTrailer(movie.getTrailer());
        dto.setImageURL1(movie.getImageURL1());
        dto.setImageURL2(movie.getImageURL2());
        dto.setTotalLikes((long) (movie.getLikes() != null ? movie.getLikes().size() : 0));

        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                dto.setUserLiked(likeRepository.existsByUserAndMovie(user, movie));
                dto.setInUserList(minhaListaRepository.existsByUserAndMovie(user, movie));
            }
        } else {
            dto.setInUserList(null);
            dto.setUserLiked(null);
        }
        return dto;
    }

    public PaginatedResponseDTO<MovieSimpleDTO> createPaginatedResponse(Page<Movie> mediaPage) {
        List<MovieSimpleDTO> mediaDTOs = mediaPage.getContent().stream()
                .map(this::convertToSimpleDTO)
                .collect(Collectors.toList());

        return new PaginatedResponseDTO<>(
                mediaDTOs,
                mediaPage.getNumber(),
                mediaPage.getTotalPages(),
                mediaPage.getTotalElements(),
                mediaPage.getSize(),
                mediaPage.isFirst(),
                mediaPage.isLast(),
                mediaPage.hasNext(),
                mediaPage.hasPrevious()
        );
    }
}