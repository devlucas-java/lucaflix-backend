package com.lucaflix.dto.admin.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyStatsDTO {
    private Integer year;
    private Integer month;
    private String monthName;
    private Long addedMovies;
    private Long addedSeries;
    private Long totalLikes;
    private Long newUsers;
    private Double averageRating;
}
