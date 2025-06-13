package com.lucaflix.dto.media;

import com.lucaflix.model.enums.Categoria;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class SearchResultDTO {
    private Long id;
    private String title;
    private String type; // "movie" ou "serie"
    private Date anoLancamento;
    private List<Categoria> categoria;
    private String sinopse;
    private Double avaliacao;
    private String imageURL1;
    private String imageURL2;
    private String minAge;

    // Campos específicos para filmes
    private Integer duracaoMinutos;

    private Integer totalTemporadas;
    private Integer totalEpisodios;
}