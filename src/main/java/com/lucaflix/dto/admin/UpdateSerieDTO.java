package com.lucaflix.dto.admin;

import com.lucaflix.model.enums.Categories;
import lombok.Data;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.util.List;

@Data
public class UpdateSerieDTO {

    @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    private String title;

    private Integer anoLancamento;

    private String tmdbId;

    private String imdbId;

    private String paisOrigen;

    @Size(max = 5000, message = "Sinopse deve ter no máximo 5000 caracteres")
    private String sinopse;

    private List<Categories> categories;

    private String minAge;

    @DecimalMin(value = "0.0", message = "Avaliação deve ser no mínimo 0.0")
    @DecimalMax(value = "10.0", message = "Avaliação deve ser no máximo 10.0")
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

}