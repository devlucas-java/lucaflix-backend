package com.lucaflix.dto.request.anime;

import com.lucaflix.model.enums.Categories;
import lombok.Data;

@Data
public class AnimeFilter {
    private String title;
    private Double rating;
    private Categories categories;
    private Integer year;
    private Integer minSeason;
    private Integer maxSeason;
    private Integer minEpisodes;
    private Integer maxEpisodes;
    private String countryOrigin;
}