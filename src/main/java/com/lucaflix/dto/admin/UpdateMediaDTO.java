package com.lucaflix.dto.admin;

import com.lucaflix.model.enums.Categoria;
import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.Date;
import java.util.List;

@Data
public class UpdateMediaDTO {

    private String title;
    private Boolean isFilme;
    private Date anoLancamento;

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
    private String trailer;
    private String imageURL1;
    private String imageURL2;
}