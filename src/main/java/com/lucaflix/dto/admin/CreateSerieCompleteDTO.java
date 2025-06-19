package com.lucaflix.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lucaflix.model.enums.Categoria;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CreateSerieCompleteDTO {

    @NotBlank(message = "Título é obrigatório")
    private String title;

    private String sinopse;

    @NotEmpty(message = "Pelo menos uma categoria é obrigatória")
    private List<Categoria> categoria;

    private Integer ano;

    private String tmdbId;

    private String imdbId;

    private String paisOrigem;

    private String trailer;

    private Double avaliacao;

    private String minAge;

    private String imageURL1;

    private String imageURL2;

    @Valid
    @NotEmpty(message = "Pelo menos uma temporada é obrigatória")
    private List<CreateTemporadaCompleteDTO> temporadas;

    @Data
    public static class CreateTemporadaCompleteDTO {
        @NotNull(message = "Número da temporada é obrigatório")
        private Integer numeroTemporada;

        private Integer anoLancamento;

        @Valid
        @NotEmpty(message = "Pelo menos um episódio é obrigatório")
        private List<CreateEpisodioCompleteDTO> episodios;
    }

    @Data
    public static class CreateEpisodioCompleteDTO {
        @NotNull(message = "Número do episódio é obrigatório")
        private Integer numeroEpisodio;

        @NotBlank(message = "Nome do episódio é obrigatório")
        private String title;

        private String sinopse;

        private Integer duracaoMinutos;

        private String embed1;

        private String embed2;
    }
}