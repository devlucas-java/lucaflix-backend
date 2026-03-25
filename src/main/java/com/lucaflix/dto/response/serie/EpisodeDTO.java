package com.lucaflix.dto.response.serie;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EpisodeDTO {

    private Long id;
    private Integer numberEpisode;
    private String title;
    private String synopsis;
    private Integer minutesDuration;
    private LocalDate dateRegistered;
    private String embed1;
    private String embed2;
}