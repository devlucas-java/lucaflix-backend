package com.lucaflix.dto.admin.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrendStatsDTO {
    private List<MonthlyStatsDTO> monthlyTrends;
    private List<CategoryStatsDTO> trendingCategories; // Categorias em alta
    private List<TopItemDTO> trendingItems; // Itens com mais likes recentes
    private Double growthRate; // Taxa de crescimento mensal
    private String fastestGrowingCategory;
}
