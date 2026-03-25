package com.lucaflix.repository;

import com.lucaflix.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // Para filmes
    boolean existsByUserAndMovie(User user, Movie movie);
    Optional<Like> findByUserAndMovie(User user, Movie movie);
    void deleteByMovie(Movie movie);

    // Para séries
    Optional<Like> findByUserAndSerie(User user, Series series);
    void deleteBySerie(Series series);

    // Para animes
    boolean existsByUserAndAnime(User user, Anime anime);
    Optional<Like> findByUserAndAnime(User user, Anime anime);
    void deleteByAnime(Anime anime);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.movie IS NOT NULL")
    long countByMovieIsNotNull();

    @Query("SELECT COUNT(l) FROM Like l WHERE l.serie IS NOT NULL")
    long countBySerieIsNotNull();

    long countBySerie(Series series);

    long countByMovie(Movie movie);

    long countByAnime(Anime anime);
}