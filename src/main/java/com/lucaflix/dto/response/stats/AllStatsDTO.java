package com.lucaflix.dto.response.stats;

import lombok.Data;

import java.util.Date;

@Data
public class AllStatsDTO {

    private long totalMovies;
    private long totalSeries;
    private long totalAnimes;
    private long totalUsers;
    private long totalLikes;
    private long totalListItems;
    private Date lastUpdate;
    
}
