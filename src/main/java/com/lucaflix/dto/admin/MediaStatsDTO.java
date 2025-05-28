package com.lucaflix.dto.admin;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaStatsDTO {
    private long totalMedias;
    private long totalFilmes;
    private long totalSeries;
    private long totalLikes;
    private long totalUsersWithLists;
    private double averageRating;
    private String mostLikedMediaTitle;
    private String mostPopularCategory;
}