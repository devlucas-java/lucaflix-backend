package com.lucaflix.repository;

import com.lucaflix.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {


    boolean existsByUserAndMovie(User user, Movie movie);

    boolean existsByUserAndAnime(User user, Anime anime);

    boolean existsByUserAndSeries(User user, Series series);

    Long countByMovieIsNotNull();

    Long countBySeriesIsNotNull();

    Long countByAnimeIsNotNull();

    Long countBySeries(Series series);

    Long countByAnime(Anime anime);

    Long countByMovie(Movie movie);

}