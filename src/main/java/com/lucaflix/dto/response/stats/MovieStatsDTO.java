package com.lucaflix.dto.response.stats;

import lombok.Data;

@Data
public class MovieStatsDTO {
    private long totalMovies;
    private double averageRating;

    private long highRatedMovies;
    private long mediumRatedMovies;
    private long lowRatedMovies;

    private long totalLikes;
    private double averageLikesPerMovie;
    private long totalListItems;
    private double averageListItemsPerMovie;
}