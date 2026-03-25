package com.lucaflix.dto.response.movie;

import com.lucaflix.model.enums.Categories;
import lombok.Data;

import java.util.List;

@Data
public class MovieSimpleDTO {
    private Long id;
    private String title;
    private String type = "MOVIE";
    private Integer yearRelease;
    private Integer minutesDuration;
    private String tmdbId;
    private String imdbId;
    private String countryOrigin;
    private List<Categories> categories;
    private String minAge;
    private Double rating;

    private String posterURL1;
    private String posterURL2;

    private Long totalLikes;
}