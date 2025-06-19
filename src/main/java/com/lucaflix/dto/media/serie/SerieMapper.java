package com.lucaflix.dto.media.serie;

import com.lucaflix.dto.media.PaginatedResponseDTO;
import com.lucaflix.model.Episodio;
import com.lucaflix.model.Serie;
import com.lucaflix.model.Temporada;
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

    public SerieSimpleDTO convertToSimpleDTO(Serie serie) {
        SerieSimpleDTO dto = new SerieSimpleDTO();
        dto.setId(serie.getId());
        dto.setTitle(serie.getTitle());
        dto.setAnoLancamento(serie.getAnoLancamento());
        dto.setTmdbId(serie.getTmdbId());
        dto.setImdbId(serie.getImdbId());
        dto.setPaisOrigem(serie.getPaisOrigem());
        dto.setCategoria(serie.getCategoria());
        dto.setMinAge(serie.getMinAge());
        dto.setAvaliacao(serie.getAvaliacao());
        dto.setImageURL1(serie.getImageURL1());
        dto.setImageURL2(serie.getImageURL2());
        dto.setTotalTemporadas(serie.getTotalTemporadas());
        dto.setTotalEpisodios(serie.getTotalEpisodios());
        dto.setTotalLikes((long) (serie.getLikes() != null ? serie.getLikes().size() : 0));
        return dto;
    }

    public SerieCompleteDTO convertToCompleteDTO(Serie serie, UUID userId) {
        SerieCompleteDTO dto = new SerieCompleteDTO();
        dto.setId(serie.getId());
        dto.setTitle(serie.getTitle());
        dto.setAnoLancamento(serie.getAnoLancamento());
        dto.setTmdbId(serie.getTmdbId());
        dto.setImdbId(serie.getImdbId());
        dto.setPaisOrigem(serie.getPaisOrigem());
        dto.setSinopse(serie.getSinopse());
        dto.setDataCadastro(serie.getDataCadastro());
        dto.setCategoria(serie.getCategoria());
        dto.setMinAge(serie.getMinAge());
        dto.setAvaliacao(serie.getAvaliacao());
        dto.setTrailer(serie.getTrailer());
        dto.setImageURL1(serie.getImageURL1());
        dto.setImageURL2(serie.getImageURL2());
        dto.setTotalTemporadas(serie.getTotalTemporadas());
        dto.setTotalEpisodios(serie.getTotalEpisodios());
        dto.setTotalLikes((long) (serie.getLikes() != null ? serie.getLikes().size() : 0));

        // Converter temporadas e episódios
        if (serie.getTemporadas() != null) {
            List<SerieCompleteDTO.TemporadaDTO> temporadasDTO = serie.getTemporadas().stream()
                    .sorted((t1, t2) -> t1.getNumeroTemporada().compareTo(t2.getNumeroTemporada()))
                    .map(this::convertToTemporadaDTO)
                    .collect(Collectors.toList());
            dto.setTemporadas(temporadasDTO);
        }

        // CORRIGIDO: Verificar se usuário curtiu e se está na lista (tratando userId null)
        if (userId != null) {
            try {
                dto.setUserLiked(serieRepository.existsLikeByUserAndSerie(userId, serie.getId()));
                dto.setInUserList(serieRepository.existsInMyListByUserAndSerie(userId, serie.getId()));
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

    public SerieCompleteDTO.TemporadaDTO convertToTemporadaDTO(Temporada temporada) {
        SerieCompleteDTO.TemporadaDTO dto = new SerieCompleteDTO.TemporadaDTO();
        dto.setId(temporada.getId());
        dto.setNumeroTemporada(temporada.getNumeroTemporada());
        dto.setAnoLancamento(temporada.getAnoLancamento());
        dto.setDataCadastro(temporada.getDataCadastro());
        dto.setTotalEpisodios(temporada.getTotalEpisodios());

        // Converter episódios
        if (temporada.getEpisodios() != null) {
            List<SerieCompleteDTO.EpisodioDTO> episodiosDTO = temporada.getEpisodios().stream()
                    .sorted((e1, e2) -> e1.getNumeroEpisodio().compareTo(e2.getNumeroEpisodio()))
                    .map(this::convertToEpisodioDTO)
                    .collect(Collectors.toList());
            dto.setEpisodios(episodiosDTO);
        }

        return dto;
    }

    public SerieCompleteDTO.EpisodioDTO convertToEpisodioDTO(Episodio episodio) {
        SerieCompleteDTO.EpisodioDTO dto = new SerieCompleteDTO.EpisodioDTO();
        dto.setId(episodio.getId());
        dto.setNumeroEpisodio(episodio.getNumeroEpisodio());
        dto.setTitle(episodio.getTitle());
        dto.setSinopse(episodio.getSinopse());
        dto.setDuracaoMinutos(episodio.getDuracaoMinutos());
        dto.setDataCadastro(episodio.getDataCadastro());
        dto.setEmbed1(episodio.getEmbed1());
        dto.setEmbed2(episodio.getEmbed2());
        return dto;
    }

    public PaginatedResponseDTO<SerieSimpleDTO> createPaginatedResponse(Page<Serie> seriesPage) {
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