package com.lucaflix.dto.response.serie;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EpisodeDTO {

    private Long id;
    private Integer numberEpisode;
    private String title;
    private String synopsis;
    private Integer minutesDuration;
    private LocalDateTime dateRegistered;
    private String embed1;
    private String embed2;
}