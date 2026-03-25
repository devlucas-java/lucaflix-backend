package com.lucaflix.dto.response.serie;

import com.lucaflix.model.enums.Categories;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class SerieCompleteDTO {
    private Long id;
    private String title;
    private String type = "SERIE";
    private Integer anoLancamento;
    private String tmdbId;
    private String imdbId;
    private String paisOrigen;
    private String sinopse;
    private Date dataCadastro;
    private List<Categories> categories;
    private String minAge;
    private Double avaliacao;

    private String trailer;

    private String posterURL1;
    private String posterURL2;

    private String backdropURL1;
    private String backdropURL2;
    private String backdropURL3;
    private String backdropURL4;

    private String logoURL1;
    private String logoURL2;

    private Integer totalTemporadas;
    private Integer totalEpisodios;
    private Long totalLikes;
    private Boolean userLiked;
    private Boolean inUserList;

    private List<TemporadaDTO> temporadas;

    @Data
    public static class TemporadaDTO {
        private Long id;
        private Integer numeroTemporada;
        private Integer anoLancamento;
        private Date dataCadastro;
        private Integer totalEpisodios;
        private List<EpisodioDTO> episodios;
    }

    @Data
    public static class EpisodioDTO {
        private Long id;
        private Integer numeroEpisodio;
        private String title;
        private String sinopse;
        private Integer duracaoMinutos;
        private Date dataCadastro;
        private String embed1;
        private String embed2;
    }
}