package com.lucaflix.dto.response.anime;

import com.lucaflix.model.enums.Categories;
import lombok.Data;

import java.util.List;

@Data
public class AnimeSimpleDTO {

    private Long id;
    private String title;
    private String type = "ANIME";
    private Integer yearRealese;
    private String tmdbId;
    private String imdbId;
    private String countryOrigin;
    private List<Categories> categories;
    private String minAge;
    private Double rating;
    private String embed1;
    private String embed2;
    private String trailer;

    private String posterURL1;
    private String posterURL2;

    private Integer totalSeason;
    private Integer totalEpisodes;

    private boolean userLiked;
    private boolean inUserList;
    private Long totalLikes;
}