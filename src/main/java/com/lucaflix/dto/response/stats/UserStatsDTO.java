package com.lucaflix.dto.response.stats;

import lombok.Data;

@Data
public class UserStatsDTO {
    private long totalUsers;
    private long activeUsers;
    private long usersWithLists;
    private double userEngagementRate; // % de usuários com listas

    // Atividade média
    private double averageLikesPerUser;
    private double averageListItemsPerUser;
}
