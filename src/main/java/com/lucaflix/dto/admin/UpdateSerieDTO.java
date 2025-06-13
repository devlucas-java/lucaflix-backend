package com.lucaflix.dto.admin;

import com.lucaflix.model.enums.Categoria;
import lombok.Data;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.util.Date;
import java.util.List;

@Data
public class UpdateSerieDTO {

    @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    private String title;

    private Date anoLancamento;

    private String tmdbId;

    private String imdbId;

    private String paisOrigem;

    @Size(max = 5000, message = "Sinopse deve ter no máximo 5000 caracteres")
    private String sinopse;

    private List<Categoria> categoria;

    private String minAge;

    @DecimalMin(value = "0.0", message = "Avaliação deve ser no mínimo 0.0")
    @DecimalMax(value = "10.0", message = "Avaliação deve ser no máximo 10.0")
    private Double avaliacao;

    private String trailer;

    private String imageURL1;

    private String imageURL2;

    private String status; // "Em exibição", "Finalizada", "Cancelada"
}