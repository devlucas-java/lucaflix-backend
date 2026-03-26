package com.lucaflix.dto.response.movie;

import com.lucaflix.model.enums.Categories;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MovieSimpleDTO {

    private UUID id;
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

    private boolean userLiked;
    private boolean inUserList;
    private Long totalLikes;
}