package com.lucaflix.dto.admin.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QualityStatsDTO {
    private Long highRatedMovies; // Avaliação >= 8.0
    private Long mediumRatedMovies; // Avaliação 6.0-7.9
    private Long lowRatedMovies; // Avaliação < 6.0

    private Long highRatedSeries; // Avaliação >= 8.0
    private Long mediumRatedSeries; // Avaliação 6.0-7.9
    private Long lowRatedSeries; // Avaliação < 6.0

    private Long highRatedAnimes; // Avaliação >= 8.0
    private Long mediumRatedAnimes; // Avaliação 6.0-7.9
    private Long lowRatedAnimes; // Avaliação < 6.0

    private Double overallQualityScore; // Pontuação geral de qualidade
}
