package com.lucaflix.dto.request.serie;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateEpisodeDTO {

    @Min(value = 1, message = "Episode number must be greater than 0")
    private Integer numberEpisode;

    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    @Size(max = 2000, message = "Synopsis must be at most 2000 characters")
    private String synopsis;

    @Min(value = 1, message = "Duration must be greater than 0")
    private Integer minutesDuration;

    private String embed1;
    private String embed2;
}