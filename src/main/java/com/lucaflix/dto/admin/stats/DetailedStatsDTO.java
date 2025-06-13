package com.lucaflix.dto.admin.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailedStatsDTO {
    private MediaStatsDTO basicStats;
    private List<CategoryStatsDTO> categoryStats;
    private List<YearStatsDTO> yearStats;
    private UserStatsDTO userStats;
    private List<TopItemDTO> topLikedMovies;
    private List<TopItemDTO> topLikedSeries;
    private QualityStatsDTO qualityStats;
}
