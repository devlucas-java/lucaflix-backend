package com.lucaflix.dto.response.serie;

import com.lucaflix.model.enums.Categories;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class SerieCompleteDTO {

    private UUID id;
    private String title;
    private String type = "SERIE";
    private Integer yearRelease;
    private String tmdbId;
    private String imdbId;
    private String countryOrigin;
    private String synopsis;
    private LocalDate dateRegistered;
    private List<Categories> categories;
    private String minAge;
    private Double rating;

    private String trailer;

    private String posterURL1;
    private String posterURL2;

    private String backdropURL1;
    private String backdropURL2;
    private String backdropURL3;
    private String backdropURL4;

    private String logoURL1;
    private String logoURL2;

    private long totalSeason;
    private Long totalLikes;
    private Boolean userLiked;
    private Boolean inMyList;
}