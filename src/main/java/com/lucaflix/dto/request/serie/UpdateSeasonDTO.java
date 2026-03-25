package com.lucaflix.dto.request.serie;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateSeasonDTO {

    @Min(value = 1, message = "Season number must be greater than 0")
    private Integer numberSeason;

    private Integer yearRelease;
}