package com.lucaflix.dto.admin.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YearStatsDTO {
    private Integer year;
    private Long totalMovies = 0L;
    private Long totalSeries = 0L;
    private Long totalItems = 0L;
    private Long totalAnimes = 0L;
    private Double averageRating;
}
