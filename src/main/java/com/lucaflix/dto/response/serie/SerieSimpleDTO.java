package com.lucaflix.dto.response.serie;

import com.lucaflix.model.enums.Categories;
import lombok.Data;
import java.util.List;

@Data
public class SerieSimpleDTO {

    private Long id;
    private String title;
    private String type = "SERIE";
    private Integer yearRelease;
    private String tmdbId;
    private String imdbId;
    private String countryOrigin;
    private List<Categories> categories;
    private String minAge;
    private Double rating;

    private String posterURL1;
    private String posterURL2;

    private Integer totalSeason;
    private Integer totalEpisodes;
    private Long totalLikes;
}