package com.lucaflix.dto.response.others;

import lombok.Data;
import java.util.Date;

// ==================== DTO PARA TODAS AS ESTATÍSTICAS ====================
@Data
public class AllStatsDTO {
    private GeneralStatsDTO general;
    private MovieStatsDTO movies;
    private SerieStatsDTO series;
    private AnimeStatsDTO animes;
    private UserStatsDTO users;

    // ==================== ESTATÍSTICAS GERAIS ====================
    @Data
    public static class GeneralStatsDTO {
        private long totalMovies;
        private long totalSeries;
        private long totalAnimes;
        private long totalUsers;
        private long totalLikes;
        private long totalListItems;
        private Date lastUpdate;
    }

    // ==================== ESTATÍSTICAS DE FILMES ====================
    @Data
    public static class MovieStatsDTO {
        private long totalMovies;
        private double averageRating;

        // Por qualidade
        private long highRatedMovies;    // >= 8.0
        private long mediumRatedMovies;  // 6.0 - 7.9
        private long lowRatedMovies;     // < 6.0

        // Engagement
        private long totalLikes;
        private double averageLikesPerMovie;
        private long totalListItems;
        private double averageListItemsPerMovie;
    }

    // ==================== ESTATÍSTICAS DE SÉRIES ====================
    @Data
    public static class SerieStatsDTO {
        private long totalSeries;
        private double averageRating;

        // Por qualidade
        private long highRatedSeries;    // >= 8.0
        private long mediumRatedSeries;  // 6.0 - 7.9
        private long lowRatedSeries;     // < 6.0

        // Engagement
        private long totalLikes;
        private double averageLikesPerSerie;
        private long totalListItems;
        private double averageListItemsPerSerie;
    }

    // ==================== ESTATÍSTICAS DE ANIMES ====================
    @Data
    public static class AnimeStatsDTO {
        private long totalAnimes;
        private double averageRating;

        // Por qualidade
        private long highRatedAnimes;    // >= 8.0
        private long mediumRatedAnimes;  // 6.0 - 7.9
        private long lowRatedAnimes;     // < 6.0

        // Engagement
        private long totalLikes;
        private double averageLikesPerAnime;
        private long totalListItems;
        private double averageListItemsPerAnime;
    }

    // ==================== ESTATÍSTICAS DE USUÁRIOS ====================
    @Data
    public static class UserStatsDTO {
        private long totalUsers;
        private long activeUsers;
        private long usersWithLists;
        private double userEngagementRate; // % de usuários com listas

        // Atividade média
        private double averageLikesPerUser;
        private double averageListItemsPerUser;
    }
}