package com.lucaflix.dto.request.serie;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import java.util.List;

@Data
public class CreateSeasonDTO {

    @NotNull(message = "Season number is required")
    private Integer numberSeason;

    private Integer yearRelease;

    @Valid
    @NotEmpty(message = "At least one episode is required")
    private List<CreateEpisodeDTO> episodes;
}