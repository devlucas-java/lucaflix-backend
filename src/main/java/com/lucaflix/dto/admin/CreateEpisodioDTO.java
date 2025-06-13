package com.lucaflix.dto.admin;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Data
public class CreateEpisodioDTO {

    @NotNull(message = "Número do episódio é obrigatório")
    @Min(value = 1, message = "Número do episódio deve ser maior que 0")
    private Integer numeroEpisodio;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    private String title;

    @Size(max = 2000, message = "Sinopse deve ter no máximo 2000 caracteres")
    private String sinopse;

    @NotNull(message = "Duração em minutos é obrigatória")
    @Min(value = 1, message = "Duração deve ser maior que 0")
    private Integer duracaoMinutos;

    private String embed1;

    private String embed2;
}