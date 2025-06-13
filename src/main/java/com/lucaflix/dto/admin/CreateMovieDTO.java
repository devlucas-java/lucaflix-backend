package com.lucaflix.dto.admin;

import com.lucaflix.model.enums.Categoria;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.Date;
import java.util.List;

@Data
public class CreateMovieDTO {

    @NotBlank(message = "Título é obrigatório")
    private String title;

    @NotNull(message = "Ano de lançamento é obrigatório")
    private Date anoLancamento;

    @Min(value = 1, message = "Duração deve ser maior que 0")
    private Integer duracaoMinutos;

    private String sinopse;

    @NotNull(message = "Categoria é obrigatória")
    private List<Categoria> categoria;

    private String minAge;

    @Min(value = 0, message = "Avaliação deve ser no mínimo 0")
    @Max(value = 10, message = "Avaliação deve ser no máximo 10")
    private Double avaliacao;

    private String embed1;
    private String embed2;
    private String trailer;
    private String imageURL1;
    private String imageURL2;
}