package com.lucaflix.dto.response.serie;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class SeasonDTO {

    private Long id;
    private Integer numberSeason;
    private Integer yearRelease;
    private LocalDate dateRegistered;
    private Integer totalEpisodes;
    private List<EpisodeDTO> episodes;
}