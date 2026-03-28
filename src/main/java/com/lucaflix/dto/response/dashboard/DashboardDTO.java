package com.lucaflix.dto.response.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardDTO {

    private UserDashboardDTO users;
    private MovieDashboardDTO movies;
    private SeriesDashboardDTO series;
    private AnimeDashboardDTO animes;
}
