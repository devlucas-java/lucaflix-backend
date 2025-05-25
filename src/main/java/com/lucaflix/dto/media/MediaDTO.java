package com.lucaflix.dto.media;

import com.lucaflix.model.enums.Categoria;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

public class MediaDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilmeResponse {
        private Long id;
        private String title;
        private Date anoLancamento;
        private String sinopse;
        private Date dateCadastro;
        private Categoria categoria;
        private String trailer;
        private String imageURL;
        private Long totalLikes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilmeDetailsResponse {
        private Long id;
        private String title;
        private Date anoLancamento;
        private String sinopse;
        private Date dateCadastro;
        private Categoria categoria;
        private String embed1;
        private String embed2;
        private String trailer;
        private String imageURL;
        private Long totalLikes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SerieResponse {
        private Long id;
        private String title;
        private Date anoLancamento;
        private String sinopse;
        private Date dateCadastro;
        private Categoria categoria;
        private String trailer;
        private String imageURL;
        private Long totalLikes;
        private Integer totalTemporadas;
        private Integer totalEpisodios;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SerieDetailsResponse {
        private Long id;
        private String title;
        private Date anoLancamento;
        private String sinopse;
        private Date dateCadastro;
        private Categoria categoria;
        private String trailer;
        private String imageURL;
        private Long totalLikes;
        private List<TemporadaResponse> temporadas;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TemporadaResponse {
        private Long id;
        private Short numeroTemporada;
        private String titulo;
        private List<EpisodioResponse> episodios;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EpisodioResponse {
        private Long id;
        private String titulo;
        private Short numeroEpisodio;
        private String sinopse;
        private String embedUrl;
        private Integer duracaoMinutos;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserWatchedContentResponse {
        private List<FilmeResponse> filmesAssistidos;
        private List<SerieResponse> seriesAssistindo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MinhaListaResponse {
        private List<FilmeResponse> filmes;
        private List<SerieResponse> series;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilteredContentResponse {
        private List<FilmeResponse> filmes;
        private List<SerieResponse> series;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResultResponse {
        private List<FilmeResponse> filmes;
        private List<SerieResponse> series;
        private Integer totalResults;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeResponse {
        private Boolean liked;
        private Long totalLikes;
    }
}