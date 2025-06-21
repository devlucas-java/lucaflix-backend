package com.lucaflix.dto.admin;

import com.lucaflix.model.enums.Categoria;
import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.Date;
import java.util.List;

@Data
public class UpdateMovieDTO {

    private String title;
    private Integer anoLancamento;

    @Min(value = 1, message = "Duração deve ser maior que 0")
    private Integer duracaoMinutos;

    private String sinopse;
    private List<Categoria> categoria;
    private String minAge;

    @Min(value = 0, message = "Avaliação deve ser no mínimo 0")
    @Max(value = 10, message = "Avaliação deve ser no máximo 10")
    private Double avaliacao;

    private String embed1;
    private String embed2;
    private String tmdbId;
    private String imdbId;
    private String paisOrigen;

    private String trailer;

    private String posterURL1;
    private String posterURL2;

    private String backdropURL1;
    private String backdropURL2;
    private String backdropURL3;
    private String backdropURL4;

    private String logoURL1;
    private String logoURL2;

}