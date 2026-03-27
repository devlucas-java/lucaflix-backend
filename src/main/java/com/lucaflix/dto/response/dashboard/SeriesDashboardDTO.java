package com.lucaflix.dto.response.dashboard;

import lombok.Data;

@Data
public class SeriesDashboardDTO {

    private long totalSeries;

    private long totalLikes;

    private double averageRating;
}
