package com.lucaflix.dto.admin.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryStatsDTO {
    private String categoria;
    private Long totalMovies = 0L;
    private Long totalSeries = 0L;
    private Long totalItems = 0L;
    private Double averageRating;
    private Long totalLikes = 0L;
}
