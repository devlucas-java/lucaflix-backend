package com.lucaflix.dto.admin;

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

    // Dados básicos da série
    @NotBlank(message = "Título é obrigatório")
    private String title;

    private String sinopse;

    @NotEmpty(message = "Pelo menos uma categoria é obrigatória")
    private List<Categoria> categoria;

    private Integer ano;

    private String tmdbId;

    private String imdbId;

    private String paisOrigem;

    private String capa;

    private String poster;

    private String trailer;

    private Double avaliacao;

    private Integer duracao;

    private String idadeRecomendada;

    private String minAge;

    private String imageURL1;

    private String imageURL2;

    private Date anoLancamento;

    private String embedId;

    // Lista de temporadas com episódios
    @Valid
    @NotEmpty(message = "Pelo menos uma temporada é obrigatória")
    private List<CreateTemporadaCompleteDTO> temporadas;

    @Data
    public static class CreateTemporadaCompleteDTO {
        @NotNull(message = "Número da temporada é obrigatório")
        private Integer temporada;

        private Integer anoLancamento;

        private Integer totalEpisodios;

        @Valid
        @NotEmpty(message = "Pelo menos um episódio é obrigatório")
        private List<CreateEpisodioCompleteDTO> episodios;
    }

    @Data
    public static class CreateEpisodioCompleteDTO {
        @NotNull(message = "Número do episódio é obrigatório")
        private Integer episodio;

        @NotBlank(message = "Nome do episódio é obrigatório")
        private String nome;

        private String imdbId;

        private Integer duracao;

        private String sinopse;

        private String embed1;

        private String embed2;

        private Integer duracaoMinutos;
    }
}