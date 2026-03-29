package com.lucaflix.service;

import com.lucaflix.dto.response.dashboard.*;
import com.lucaflix.model.enums.Plan;
import com.lucaflix.model.enums.Role;
import com.lucaflix.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final SeriesRepository seriesRepository;
    private final AnimeRepository animeRepository;
    private final LikeRepository likeRepository;

    public DashboardDTO getDashboard() {
        return DashboardDTO.builder()
                .users(buildUserDashboard())
                .movies(buildMovieDashboard())
                .series(buildSeriesDashboard())
                .animes(buildAnimeDashboard())
                .build();
    }

    private UserDashboardDTO buildUserDashboard() {
        UserDashboardDTO dto = new UserDashboardDTO();

        dto.setTotalUsers(userRepository.count());

        dto.setFreePlan(userRepository.countByPlan(Plan.FREE));
        dto.setPremiumPlan(userRepository.countByPlan(Plan.PREMIUM));
        dto.setMaxPlan(userRepository.countByPlan(Plan.MAXIMUM));

        dto.setBlockedUsers(userRepository.countByIsAccountLockedTrue());

        dto.setAdmins(userRepository.countByRole(Role.ADMIN));
        dto.setSuperAdmins(userRepository.countByRole(Role.SUPERADMIN));
        dto.setUsers(userRepository.countByRole(Role.USER));

        return dto;
    }

    private MovieDashboardDTO buildMovieDashboard() {
        MovieDashboardDTO dto = new MovieDashboardDTO();

        dto.setTotalMovies(movieRepository.count());
        dto.setTotalLikes(likeRepository.countByMovieIsNotNull());
        dto.setAverageRating(getSafeAverage(movieRepository.getAverageRating()));

        return dto;
    }

    private SeriesDashboardDTO buildSeriesDashboard() {
        SeriesDashboardDTO dto = new SeriesDashboardDTO();

        dto.setTotalSeries(seriesRepository.count());
        dto.setTotalLikes(likeRepository.countBySeriesIsNotNull());
        dto.setAverageRating(getSafeAverage(seriesRepository.getAverageRating()));

        return dto;
    }

    private AnimeDashboardDTO buildAnimeDashboard() {
        AnimeDashboardDTO dto = new AnimeDashboardDTO();

        dto.setTotalAnimes(animeRepository.count());
        dto.setTotalLikes(likeRepository.countByAnimeIsNotNull());
        dto.setAverageRating(getSafeAverage(animeRepository.getAverageRating()));

        return dto;
    }

    private double getSafeAverage(Double value) {
        if (value == null) return 0.0;

        double v = Math.clamp(value, 0.0, 10.0);
        return Math.round(v * 100.0) / 100.0;
    }
}