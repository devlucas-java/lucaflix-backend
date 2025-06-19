package com.lucaflix.dto.media.anime;

import com.lucaflix.model.enums.Categoria;
import lombok.Data;

@Data
public class AnimeFilter {
    private String title;
    private Double avaliacao;
    private Categoria categoria;
    private Integer year;
    private Integer minTemporadas;
    private Integer maxTemporadas;
    private Integer minEpisodios;
    private Integer maxEpisodios;
    private String paisOrigen;
}