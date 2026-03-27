package com.lucaflix.dto.response.serie;

import com.lucaflix.model.enums.Categories;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class SerieSimpleDTO {

    private UUID id;
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

    private long totalSeason;
    private boolean userLiked;
    private boolean inMyList;
    private Long totalLikes;
}