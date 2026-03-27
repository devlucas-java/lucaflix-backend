package com.lucaflix.dto.response.dashboard;

import lombok.Data;

@Data
public class DashboardDTO {

    private UserDashboardDTO users;
    private MovieDashboardDTO movies;
    private SeriesDashboardDTO series;
    private AnimeDashboardDTO animes;
}
