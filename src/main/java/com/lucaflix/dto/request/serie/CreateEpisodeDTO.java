package com.lucaflix.dto.request.serie;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateEpisodeDTO {

    @NotNull(message = "Episode number is required")
    private Integer numberEpisode;

    @NotBlank(message = "Title is required")
    private String title;

    @Max(value = 1000, message = "The synopsis episode must max 1000 characters")
    private String synopsis;

    private Integer minutesDuration = 0;

    private String embed1;
    private String embed2;
}