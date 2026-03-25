package com.lucaflix.service;

import com.lucaflix.dto.response.others.AllStatsDTO;
import com.lucaflix.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final MovieRepository movieRepository;
    private final SerieRepository serieRepository;
    private final AnimeRepository animeRepository;
    private final LikeRepository likeRepository;
    private final MinhaListaRepository minhaListaRepository;
    private final UserRepository userRepository;

    // ==================== ESTATÍSTICAS GERAIS ====================

    public AllStatsDTO.GeneralStatsDTO getGeneralStats() {
        AllStatsDTO.GeneralStatsDTO stats = new AllStatsDTO.GeneralStatsDTO();

        try {
            stats.setTotalMovies(movieRepository.count());
            stats.setTotalSeries(serieRepository.count());
            stats.setTotalAnimes(animeRepository.count());
            stats.setTotalUsers(userRepository.count());
            stats.setTotalLikes(likeRepository.count());
            stats.setTotalListItems(minhaListaRepository.count());
            stats.setLastUpdate(new Date());

        } catch (Exception e) {
            // Em caso de erro, retorna objeto vazio
            stats = new AllStatsDTO.GeneralStatsDTO();
            stats.setLastUpdate(new Date());
        }

        return stats;
    }

    // ==================== ESTATÍSTICAS DE FILMES ====================

    public AllStatsDTO.MovieStatsDTO getMovieStats() {
        AllStatsDTO.MovieStatsDTO stats = new AllStatsDTO.MovieStatsDTO();

        try {
            // Contadores básicos
            stats.setTotalMovies(movieRepository.count());

            // Avaliações
            Double avgRating = movieRepository.getAverageRating();
            stats.setAverageRating(avgRating != null ?
                    Math.round(avgRating * 100.0) / 100.0 : 0.0);

            // Por qualidade
            stats.setHighRatedMovies(movieRepository.countByAvaliacaoGreaterThanEqual(8.0));
            stats.setMediumRatedMovies(movieRepository.countByAvaliacaoBetween(6.0, 7.9));
            stats.setLowRatedMovies(movieRepository.countByAvaliacaoLessThan(6.0));

            // Engagement
            long movieLikes = likeRepository.countByMovieIsNotNull();
            stats.setTotalLikes(movieLikes);
            stats.setAverageLikesPerMovie(stats.getTotalMovies() > 0 ?
                    Math.round((double) movieLikes / stats.getTotalMovies() * 100.0) / 100.0 : 0.0);

            long movieListItems = minhaListaRepository.countMovieItems();
            stats.setTotalListItems(movieListItems);
            stats.setAverageListItemsPerMovie(stats.getTotalMovies() > 0 ?
                    Math.round((double) movieListItems / stats.getTotalMovies() * 100.0) / 100.0 : 0.0);

        } catch (Exception e) {
            stats = new AllStatsDTO.MovieStatsDTO();
        }

        return stats;
    }

    // ==================== ESTATÍSTICAS DE SÉRIES ====================

    public AllStatsDTO.SerieStatsDTO getSerieStats() {
        AllStatsDTO.SerieStatsDTO stats = new AllStatsDTO.SerieStatsDTO();

        try {
            // Contadores básicos
            stats.setTotalSeries(serieRepository.count());

            // Avaliações
            Double avgRating = serieRepository.getAverageRating();
            stats.setAverageRating(avgRating != null ?
                    Math.round(avgRating * 100.0) / 100.0 : 0.0);

            // Por qualidade
            stats.setHighRatedSeries(serieRepository.countByAvaliacaoGreaterThanEqual(8.0));
            stats.setMediumRatedSeries(serieRepository.countByAvaliacaoBetween(6.0, 7.9));
            stats.setLowRatedSeries(serieRepository.countByAvaliacaoLessThan(6.0));

            // Engagement
            long serieLikes = likeRepository.countBySerieIsNotNull();
            stats.setTotalLikes(serieLikes);
            stats.setAverageLikesPerSerie(stats.getTotalSeries() > 0 ?
                    Math.round((double) serieLikes / stats.getTotalSeries() * 100.0) / 100.0 : 0.0);

            long serieListItems = minhaListaRepository.countSerieItems();
            stats.setTotalListItems(serieListItems);
            stats.setAverageListItemsPerSerie(stats.getTotalSeries() > 0 ?
                    Math.round((double) serieListItems / stats.getTotalSeries() * 100.0) / 100.0 : 0.0);

        } catch (Exception e) {
            stats = new AllStatsDTO.SerieStatsDTO();
        }

        return stats;
    }

    // ==================== ESTATÍSTICAS DE ANIMES ====================

    public AllStatsDTO.AnimeStatsDTO getAnimeStats() {
        AllStatsDTO.AnimeStatsDTO stats = new AllStatsDTO.AnimeStatsDTO();

        try {
            // Contadores básicos
            stats.setTotalAnimes(animeRepository.count());

            // Avaliações
            Double avgRating = animeRepository.getAverageRating();
            stats.setAverageRating(avgRating != null ?
                    Math.round(avgRating * 100.0) / 100.0 : 0.0);

            // Por qualidade
            stats.setHighRatedAnimes(animeRepository.countByAvaliacaoGreaterThanEqual(8.0));
            stats.setMediumRatedAnimes(animeRepository.countByAvaliacaoBetween(6.0, 7.9));
            stats.setLowRatedAnimes(animeRepository.countByAvaliacaoLessThan(6.0));

            // Engagement - Anime não tem Like específico, então vamos usar a contagem geral
            long animeListItems = minhaListaRepository.countAnimeItems();
            stats.setTotalListItems(animeListItems);
            stats.setAverageListItemsPerAnime(stats.getTotalAnimes() > 0 ?
                    Math.round((double) animeListItems / stats.getTotalAnimes() * 100.0) / 100.0 : 0.0);

            // Para likes, vamos contar os likes dos animes através do relacionamento
            // Se não tiver método específico, deixamos 0 por enquanto
            stats.setTotalLikes(0);
            stats.setAverageLikesPerAnime(0.0);

        } catch (Exception e) {
            stats = new AllStatsDTO.AnimeStatsDTO();
        }

        return stats;
    }

    // ==================== ESTATÍSTICAS DE USUÁRIOS ====================

    public AllStatsDTO.UserStatsDTO getUserStats() {
        AllStatsDTO.UserStatsDTO stats = new AllStatsDTO.UserStatsDTO();

        try {
            // Contadores básicos
            stats.setTotalUsers(userRepository.count());

            // Engagement
            long usersWithLists = minhaListaRepository.countDistinctUsers();
            stats.setUsersWithLists(usersWithLists);
            stats.setUserEngagementRate(stats.getTotalUsers() > 0 ?
                    Math.round((double) usersWithLists / stats.getTotalUsers() * 100.0 * 100.0) / 100.0 : 0.0);

            // Atividade média
            long totalLikes = likeRepository.count();
            stats.setAverageLikesPerUser(stats.getTotalUsers() > 0 ?
                    Math.round((double) totalLikes / stats.getTotalUsers() * 100.0) / 100.0 : 0.0);

            long totalListItems = minhaListaRepository.count();
            stats.setAverageListItemsPerUser(stats.getTotalUsers() > 0 ?
                    Math.round((double) totalListItems / stats.getTotalUsers() * 100.0) / 100.0 : 0.0);

            // Usuários ativos (simplificado como usuários com listas)
            stats.setActiveUsers(usersWithLists);

        } catch (Exception e) {
            stats = new AllStatsDTO.UserStatsDTO();
        }

        return stats;
    }
}