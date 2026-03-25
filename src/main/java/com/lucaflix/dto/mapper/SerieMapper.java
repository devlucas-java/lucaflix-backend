package com.lucaflix.dto.mapper;

import com.lucaflix.dto.response.page.PaginatedResponseDTO;
import com.lucaflix.dto.response.serie.SerieCompleteDTO;
import com.lucaflix.dto.response.serie.SerieSimpleDTO;
import com.lucaflix.model.Episode;
import com.lucaflix.model.Series;
import com.lucaflix.model.Season;
import com.lucaflix.repository.SerieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SerieMapper {

    private final SerieRepository serieRepository;

    public SerieSimpleDTO convertToSimpleDTO(Series series) {
        SerieSimpleDTO dto = new SerieSimpleDTO();
        dto.setId(series.getId());
        dto.setTitle(series.getTitle());
        dto.setAnoLancamento(series.getAnoLancamento());
        dto.setTmdbId(series.getTmdbId());
        dto.setImdbId(series.getImdbId());
        dto.setPaisOrigen(series.getPaisOrigem());
        dto.setCategories(series.getCategoria());
        dto.setMinAge(series.getMinAge());
        dto.setAvaliacao(series.getAvaliacao());
        dto.setPosterURL1(series.getPosterURL1());
        dto.setPosterURL2(series.getPosterURL2());
        dto.setTotalTemporadas(series.getTotalTemporadas());
        dto.setTotalEpisodios(series.getTotalEpisodios());
        dto.setTotalLikes((long) (series.getLikes() != null ? series.getLikes().size() : 0));
        return dto;
    }

    public SerieCompleteDTO convertToCompleteDTO(Series series, UUID userId) {
        SerieCompleteDTO dto = new SerieCompleteDTO();
        dto.setId(series.getId());
        dto.setTitle(series.getTitle());
        dto.setAnoLancamento(series.getAnoLancamento());
        dto.setTmdbId(series.getTmdbId());
        dto.setImdbId(series.getImdbId());
        dto.setPaisOrigen(series.getPaisOrigem());
        dto.setSinopse(series.getSinopse());
        dto.setDataCadastro(series.getDataCadastro());
        dto.setCategories(series.getCategoria());
        dto.setMinAge(series.getMinAge());
        dto.setAvaliacao(series.getAvaliacao());
        dto.setTrailer(series.getTrailer());
        dto.setPosterURL1(series.getPosterURL1());
        dto.setPosterURL2(series.getPosterURL2());
        dto.setBackdropURL1(series.getBackdropURL1());
        dto.setBackdropURL2(series.getBackdropURL2());
        dto.setBackdropURL3(series.getBackdropURL3());
        dto.setBackdropURL4(series.getBackdropURL4());
        dto.setLogoURL1(series.getLogoURL1());
        dto.setLogoURL2(series.getLogoURL2());
        dto.setTotalTemporadas(series.getTotalTemporadas());
        dto.setTotalEpisodios(series.getTotalEpisodios());
        dto.setTotalLikes((long) (series.getLikes() != null ? series.getLikes().size() : 0));

        // Converter temporadas e episódios
        if (series.getTemps() != null) {
            List<SerieCompleteDTO.TemporadaDTO> temporadasDTO = series.getTemps().stream()
                    .sorted((t1, t2) -> t1.getNumeroTemporada().compareTo(t2.getNumeroTemporada()))
                    .map(this::convertToTemporadaDTO)
                    .collect(Collectors.toList());
            dto.setTemporadas(temporadasDTO);
        }

        // CORRIGIDO: Verificar se usuário curtiu e se está na lista (tratando userId null)
        if (userId != null) {
            try {
                dto.setUserLiked(serieRepository.existsLikeByUserAndSerie(userId, series.getId()));
                dto.setInUserList(serieRepository.existsInMyListByUserAndSerie(userId, series.getId()));
            } catch (Exception e) {
                log.warn("Erro ao verificar like/lista do usuário {}: {}", userId, e.getMessage());
                dto.setUserLiked(null);
                dto.setInUserList(null);
            }
        } else {
            dto.setUserLiked(null);
            dto.setInUserList(null);
        }

        return dto;
    }

    public SerieCompleteDTO.TemporadaDTO convertToTemporadaDTO(Season season) {
        SerieCompleteDTO.TemporadaDTO dto = new SerieCompleteDTO.TemporadaDTO();
        dto.setId(season.getId());
        dto.setNumeroTemporada(season.getNumeroTemporada());
        dto.setAnoLancamento(season.getAnoLancamento());
        dto.setDataCadastro(season.getDataCadastro());
        dto.setTotalEpisodios(season.getTotalEpisodios());

        // Converter episódios
        if (season.getEpisodes() != null) {
            List<SerieCompleteDTO.EpisodioDTO> episodiosDTO = season.getEpisodes().stream()
                    .sorted((e1, e2) -> e1.getNumeroEpisodio().compareTo(e2.getNumeroEpisodio()))
                    .map(this::convertToEpisodioDTO)
                    .collect(Collectors.toList());
            dto.setEpisodios(episodiosDTO);
        }

        return dto;
    }

    public SerieCompleteDTO.EpisodioDTO convertToEpisodioDTO(Episode episode) {
        SerieCompleteDTO.EpisodioDTO dto = new SerieCompleteDTO.EpisodioDTO();
        dto.setId(episode.getId());
        dto.setNumeroEpisodio(episode.getNumeroEpisodio());
        dto.setTitle(episode.getTitle());
        dto.setSinopse(episode.getSinopse());
        dto.setDuracaoMinutos(episode.getDuracaoMinutos());
        dto.setDataCadastro(episode.getDataCadastro());
        dto.setEmbed1(episode.getEmbed1());
        dto.setEmbed2(episode.getEmbed2());
        return dto;
    }

    public PaginatedResponseDTO<SerieSimpleDTO> createPaginatedResponse(Page<Series> seriesPage) {
        List<SerieSimpleDTO> seriesDTOs = seriesPage.getContent().stream()
                .map(this::convertToSimpleDTO)
                .collect(Collectors.toList());

        return new PaginatedResponseDTO<>(
                seriesDTOs,
                seriesPage.getNumber(),
                seriesPage.getTotalPages(),
                seriesPage.getTotalElements(),
                seriesPage.getSize(),
                seriesPage.isFirst(),
                seriesPage.isLast(),
                seriesPage.hasNext(),
                seriesPage.hasPrevious()
        );
    }
}