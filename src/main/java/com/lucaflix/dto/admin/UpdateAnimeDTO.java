package com.lucaflix.dto.admin;

import com.lucaflix.model.enums.Categoria;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class UpdateAnimeDTO {

    @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    private String title;

    private Integer anoLancamento;

    private String tmdbId;
    private String imdbId;
    private String paisOrigen;

    @Size(max = 5000, message = "Sinopse deve ter no máximo 5000 caracteres")
    private String sinopse;

    private List<Categoria> categoria;

    private String minAge;

    @DecimalMin(value = "0.0", message = "Avaliação deve ser maior ou igual a 0")
    @DecimalMax(value = "10.0", message = "Avaliação deve ser menor ou igual a 10")
    private Double avaliacao;

    private String embed1;
    private String embed2;

    private String trailer;

    private String posterURL1;
    private String posterURL2;

    private String backdropURL1;
    private String backdropURL2;
    private String backdropURL3;
    private String backdropURL4;

    private String logoURL1;
    private String logoURL2;


    @Min(value = 0, message = "Total de temporadas deve ser maior ou igual a 0")
    private Integer totalTemporadas;

    @Min(value = 0, message = "Total de episódios deve ser maior ou igual a 0")
    private Integer totalEpisodios;
}