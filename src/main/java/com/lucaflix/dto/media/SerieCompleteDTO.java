package com.lucaflix.dto.media;

import com.lucaflix.model.enums.Categoria;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class SerieCompleteDTO {
    private Long id;
    private String title;
    private Date anoLancamento;
    private String tmdbId;
    private String imdbId;
    private String paisOrigem;
    private String sinopse;
    private Date dataCadastro;
    private List<Categoria> categoria;
    private String minAge;
    private Double avaliacao;
    private String trailer;
    private String imageURL1;
    private String imageURL2;
    private Integer totalTemporadas;
    private Integer totalEpisodios;
    private Long totalLikes;
    private Boolean userLiked;
    private Boolean inUserList;

    // Nova propriedade para incluir temporadas com episódios
    private List<TemporadaDTO> temporadas;

    @Data
    public static class TemporadaDTO {
        private Long id;
        private Integer numeroTemporada;
        private Date anoLancamento;
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