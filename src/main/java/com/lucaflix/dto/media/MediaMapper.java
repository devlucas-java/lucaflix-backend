package com.lucaflix.dto.media;

import com.lucaflix.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MediaMapper {

    public MediaDTO.FilmeResponse toFilmeResponse(Filme filme, long totalLikes) {
        return MediaDTO.FilmeResponse.builder()
                .id(filme.getId())
                .title(filme.getTitle())
                .anoLancamento(filme.getAnoLancamento())
                .sinopse(filme.getSinopse())
                .dateCadastro(filme.getDataCadastro())
                .categoria(filme.getCategoria())
                .trailer(filme.getTrailer())
                .imageURL(filme.getImageURL())
                .totalLikes(totalLikes)
                .build();
    }

    public MediaDTO.FilmeDetailsResponse toFilmeDetailsResponse(Filme filme, long totalLikes) {
        return MediaDTO.FilmeDetailsResponse.builder()
                .id(filme.getId())
                .title(filme.getTitle())
                .anoLancamento(filme.getAnoLancamento())
                .sinopse(filme.getSinopse())
                .dateCadastro(filme.getDataCadastro())
                .categoria(filme.getCategoria())
                .embed1(filme.getEmbed1())
                .embed2(filme.getEmbed2())
                .trailer(filme.getTrailer())
                .imageURL(filme.getImageURL())
                .totalLikes(totalLikes)
                .build();
    }

    public MediaDTO.SerieResponse toSerieResponse(Serie serie, long totalLikes) {
        int totalTemporadas = serie.getTemporadas() != null ? serie.getTemporadas().size() : 0;
        int totalEpisodios = serie.getTemporadas() != null ?
                serie.getTemporadas().stream()
                        .mapToInt(temporada -> temporada.getEpisodios() != null ? temporada.getEpisodios().size() : 0)
                        .sum() : 0;

        return MediaDTO.SerieResponse.builder()
                .id(serie.getId())
                .title(serie.getTitle())
                .anoLancamento(serie.getAnoLancamento())
                .sinopse(serie.getSinopse())
                .dateCadastro(serie.getDataCadastro())
                .categoria(serie.getCategoria())
                .trailer(serie.getTrailer())
                .imageURL(serie.getImageURL())
                .totalLikes(totalLikes)
                .totalTemporadas(totalTemporadas)
                .totalEpisodios(totalEpisodios)
                .build();
    }

    public MediaDTO.SerieDetailsResponse toSerieDetailsResponse(Serie serie, long totalLikes) {
        List<MediaDTO.TemporadaResponse> temporadas = null;

        if (serie.getTemporadas() != null) {
            temporadas = serie.getTemporadas().stream()
                    .map(this::toTemporadaResponse)
                    .collect(Collectors.toList());
        }

        return MediaDTO.SerieDetailsResponse.builder()
                .id(serie.getId())
                .title(serie.getTitle())
                .anoLancamento(serie.getAnoLancamento())
                .sinopse(serie.getSinopse())
                .dateCadastro(serie.getDataCadastro())
                .categoria(serie.getCategoria())
                .trailer(serie.getTrailer())
                .imageURL(serie.getImageURL())
                .totalLikes(totalLikes)
                .temporadas(temporadas)
                .build();
    }

    public MediaDTO.TemporadaResponse toTemporadaResponse(Temporada temporada) {
        List<MediaDTO.EpisodioResponse> episodios = null;

        if (temporada.getEpisodios() != null) {
            episodios = temporada.getEpisodios().stream()
                    .map(this::toEpisodioResponse)
                    .collect(Collectors.toList());
        }

        return MediaDTO.TemporadaResponse.builder()
                .id(temporada.getId())
                .numeroTemporada(temporada.getNumeroTemporada())
                .titulo(temporada.getTitulo())
                .episodios(episodios)
                .build();
    }

    public MediaDTO.EpisodioResponse toEpisodioResponse(Episodio episodio) {
        return MediaDTO.EpisodioResponse.builder()
                .id(episodio.getId())
                .titulo(episodio.getTitulo())
                .numeroEpisodio(episodio.getNumeroEpisodio())
                .sinopse(episodio.getSinopse())
                .embedUrl(episodio.getEmbedUrl())
                .duracaoMinutos(episodio.getDuracaoMinutos())
                .build();
    }
}