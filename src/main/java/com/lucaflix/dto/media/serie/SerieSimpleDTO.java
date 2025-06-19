package com.lucaflix.dto.media.serie;

import com.lucaflix.model.enums.Categoria;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class SerieSimpleDTO {
    private Long id;
    private String title;
    private String type = "SERIE";
    private Date anoLancamento;
    private String tmdbId;
    private String imdbId;
    private String paisOrigem;
    private List<Categoria> categoria;
    private String minAge;
    private Double avaliacao;
    private String imageURL1;
    private String imageURL2;
    private Integer totalTemporadas;
    private Integer totalEpisodios;
    private Long totalLikes;
}