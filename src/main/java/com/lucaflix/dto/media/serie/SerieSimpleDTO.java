package com.lucaflix.dto.media.serie;

import com.lucaflix.model.enums.Categoria;
import jakarta.persistence.Column;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class SerieSimpleDTO {
    private Long id;
    private String title;
    private String type = "SERIE";
    private Integer anoLancamento;
    private String tmdbId;
    private String imdbId;
    private String paisOrigen;
    private List<Categoria> categoria;
    private String minAge;
    private Double avaliacao;

    private String posterURL1;
    private String posterURL2;

    private Integer totalTemporadas;
    private Integer totalEpisodios;
    private Long totalLikes;
}