package com.lucaflix.dto.response.dashboard;

import lombok.Data;

@Data
public class AnimeDashboardDTO {

    private long totalAnimes;

    private long totalLikes;

    private double averageRating;
}