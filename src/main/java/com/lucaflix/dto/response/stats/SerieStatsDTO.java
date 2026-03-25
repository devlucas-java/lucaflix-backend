package com.lucaflix.dto.response.stats;

import lombok.Data;

@Data
public class SerieStatsDTO {
    private long totalSeries;
    private double averageRating;

    private long highRatedSeries;
    private long mediumRatedSeries;
    private long lowRatedSeries;

    private long totalLikes;
    private double averageLikesPerSerie;
    private long totalListItems;
    private double averageListItemsPerSerie;
}
