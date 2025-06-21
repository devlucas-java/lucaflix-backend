package com.lucaflix.dto.admin;

import com.lucaflix.model.enums.Categoria;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.List;

@Data
public class CreateMovieDTO {

    @NotBlank(message = "Título é obrigatório")
    private String title;

    @NotNull(message = "Ano de lançamento é obrigatório")
    private Integer anoLancamento;

    @Min(value = 1, message = "Duração deve ser maior que 0")
    private Integer duracaoMinutos;

    private String sinopse;

    @NotNull(message = "Categoria é obrigatória")
    private List<Categoria> categoria;

    private String tmdbId;
    private String imdbId;
    private String paisOrigen;

    private String minAge;

    @Min(value = 0, message = "Avaliação deve ser no mínimo 0")
    @Max(value = 10, message = "Avaliação deve ser no máximo 10")
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

}