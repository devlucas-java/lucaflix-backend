package com.lucaflix.repository;

import com.lucaflix.model.Movie;
import com.lucaflix.model.Like;
import com.lucaflix.model.Serie;
import com.lucaflix.model.User;
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
    boolean existsByUserAndSerie(User user, Serie serie);
    Optional<Like> findByUserAndSerie(User user, Serie serie);
    void deleteBySerie(Serie serie);

    // Estatísticas e admin
    @Query("SELECT COUNT(l) FROM Like l")
    long countTotalLikes();

    @Query("SELECT COUNT(l) FROM Like l WHERE l.movie IS NOT NULL")
    long countMovieLikes();

    @Query("SELECT COUNT(l) FROM Like l WHERE l.serie IS NOT NULL")
    long countSerieLikes();

    @Modifying
    @Query("DELETE FROM Like l WHERE l.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
}