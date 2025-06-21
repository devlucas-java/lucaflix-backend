package com.lucaflix.dto.admin;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.util.Date;

@Data
public class CreateTemporadaDTO {

    @NotNull(message = "Número da temporada é obrigatório")
    @Min(value = 1, message = "Número da temporada deve ser maior que 0")
    private Integer numeroTemporada;

    @NotNull(message = "Ano de lançamento é obrigatório")
    private Integer anoLancamento;
}