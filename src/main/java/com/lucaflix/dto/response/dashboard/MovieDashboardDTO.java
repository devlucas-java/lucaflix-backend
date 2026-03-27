package com.lucaflix.dto.response.dashboard;

import lombok.Data;


@Data
public class MovieDashboardDTO {

    private long totalMovies;

    private long totalLikes;

    private double averageRating;
}