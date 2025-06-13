package com.lucaflix.dto.admin.stats;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaStatsDTO {
    private Long totalMedias;
    private Long totalFilmes;
    private Long totalSeries;
    private Long totalEpisodios; // Total de episódios de todas as séries
    private Long totalTemporadas; // Total de temporadas de todas as séries
    private Long totalLikes;
    private Long totalUsersWithLists;
    private Long totalUsers;
    private Double averageRating;
    private Double averageMovieRating;
    private Double averageSerieRating;
    private String mostLikedMediaTitle;
    private String mostPopularCategory;
    private Integer totalCategories; // Número de categorias diferentes em uso
    private Long moviesWithTrailer; // Filmes com trailer
    private Long seriesWithTrailer; // Séries com trailer
    private Double contentCompletionRate; // Taxa de conteúdo com embed/trailer
    private String oldestMovie;
    private String newestMovie;
    private String oldestSerie;
    private String newestSerie;
    private Integer averageMovieDuration; // Duração média dos filmes em minutos
    private Double averageEpisodesPerSerie; // Média de episódios por série
}