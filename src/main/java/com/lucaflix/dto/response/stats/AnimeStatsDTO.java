package com.lucaflix.dto.response.stats;

import lombok.Data;

@Data
public class AnimeStatsDTO {
    private long totalAnimes;
    private double averageRating;

    private long highRatedAnimes;
    private long mediumRatedAnimes;
    private long lowRatedAnimes;

    private long totalLikes;
    private double averageLikesPerAnime;
    private long totalListItems;
    private double averageListItemsPerAnime;
}