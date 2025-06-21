package com.lucaflix.dto.admin;

import lombok.Data;

import jakarta.validation.constraints.Min;
import java.util.Date;

@Data
public class UpdateTemporadaDTO {

    @Min(value = 1, message = "Número da temporada deve ser maior que 0")
    private Integer numeroTemporada;

    private Integer anoLancamento;
}