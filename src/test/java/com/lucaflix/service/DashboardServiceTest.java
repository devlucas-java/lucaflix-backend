package com.lucaflix.service;

import com.lucaflix.dto.response.dashboard.*;
import com.lucaflix.model.enums.Plan;
import com.lucaflix.model.enums.Role;
import com.lucaflix.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private AnimeRepository animeRepository;

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setup() {
        // Configuração comum (se precisares)
    }

    @Test
    void shouldBuildUserDashboardCorrectly() {
        when(userRepository.count()).thenReturn(100L);
        when(userRepository.countByPlan(Plan.FREE)).thenReturn(60L);
        when(userRepository.countByPlan(Plan.PREMIUM)).thenReturn(30L);
        when(userRepository.countByPlan(Plan.MAXIMUM)).thenReturn(10L);

        when(userRepository.countByIsAccountLockedTrue()).thenReturn(5L);

        when(userRepository.countByRole(Role.ADMIN)).thenReturn(3L);
        when(userRepository.countByRole(Role.SUPERADMIN)).thenReturn(2L);
        when(userRepository.countByRole(Role.USER)).thenReturn(95L);

        DashboardDTO dashboard = dashboardService.getDashboard();
        UserDashboardDTO users = dashboard.getUsers();

        assertEquals(100L, users.getTotalUsers());
        assertEquals(60L, users.getFreePlan());
        assertEquals(30L, users.getPremiumPlan());
        assertEquals(10L, users.getMaxPlan());
        assertEquals(5L, users.getBlockedUsers());

        assertEquals(3L, users.getAdmins());
        assertEquals(2L, users.getSuperAdmins());
        assertEquals(95L, users.getUsers());
    }

    @Test
    void shouldBuildMovieDashboardCorrectly() {
        when(movieRepository.count()).thenReturn(50L);
        when(likeRepository.countByMovieIsNotNull()).thenReturn(200L);
        when(movieRepository.getAverageRating()).thenReturn(8.456);

        DashboardDTO dashboard = dashboardService.getDashboard();
        MovieDashboardDTO movies = dashboard.getMovies();

        assertEquals(50L, movies.getTotalMovies());
        assertEquals(200L, movies.getTotalLikes());
        assertEquals(8.46, movies.getAverageRating());
    }

    @Test
    void shouldBuildSeriesDashboardCorrectly() {
        when(seriesRepository.count()).thenReturn(20L);
        when(likeRepository.countBySeriesIsNotNull()).thenReturn(80L);
        when(seriesRepository.getAverageRating()).thenReturn(9.123);

        DashboardDTO dashboard = dashboardService.getDashboard();
        SeriesDashboardDTO series = dashboard.getSeries();

        assertEquals(20L, series.getTotalSeries());
        assertEquals(80L, series.getTotalLikes());
        assertEquals(9.12, series.getAverageRating());
    }

    @Test
    void shouldBuildAnimeDashboardCorrectly() {
        when(animeRepository.count()).thenReturn(15L);
        when(likeRepository.countByAnimeIsNotNull()).thenReturn(60L);
        when(animeRepository.getAverageRating()).thenReturn(7.999);

        DashboardDTO dashboard = dashboardService.getDashboard();
        AnimeDashboardDTO anime = dashboard.getAnimes();

        assertEquals(15L, anime.getTotalAnimes());
        assertEquals(60L, anime.getTotalLikes());
        assertEquals(8.0, anime.getAverageRating());
    }

    @Test
    void shouldReturnZeroWhenAverageIsNull() {
        when(movieRepository.count()).thenReturn(0L);
        when(likeRepository.countByMovieIsNotNull()).thenReturn(0L);
        when(movieRepository.getAverageRating()).thenReturn(null);

        DashboardDTO dashboard = dashboardService.getDashboard();
        MovieDashboardDTO movies = dashboard.getMovies();

        assertEquals(0.0, movies.getAverageRating());
    }

    @Test
    void shouldClampAverageBetween0And10() {
        when(movieRepository.count()).thenReturn(1L);
        when(likeRepository.countByMovieIsNotNull()).thenReturn(1L);
        when(movieRepository.getAverageRating()).thenReturn(15.0);

        DashboardDTO dashboard = dashboardService.getDashboard();
        MovieDashboardDTO movies = dashboard.getMovies();

        assertEquals(10.0, movies.getAverageRating());
    }
}