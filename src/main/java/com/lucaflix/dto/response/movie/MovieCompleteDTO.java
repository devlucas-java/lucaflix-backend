package com.lucaflix.dto.response.movie;

import com.lucaflix.model.enums.Categories;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MovieCompleteDTO {
    private Long id;
    private String title;
    private String type = "MOVIE";
    private Integer yearRelease;
    private Integer minutesDuration;
    private String tmdbId;
    private String imdbId;
    private String paisOrigen;
    private String synopsis;
    private LocalDateTime dateRegistered;
    private List<Categories> categories;
    private Integer minAge;
    private Double rating;
    private String embed1;
    private String embed2;
    private String trailer;

    private String posterURL1;
    private String posterURL2;

    private String backdropURL1;
    private String backdropURL2;
    private String backdropURL3;
    private String backdropURL4;

    private String logoURL1;
    private String logoURL2;
    private Long totalLikes;
    private Boolean userLiked;
    private Boolean inMyList;
}