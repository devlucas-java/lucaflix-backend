package com.lucaflix.repository;

import com.lucaflix.model.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    // Busca apenas filmes
    Page<Media> findByIsFilmeTrue(Pageable pageable);

    // Busca apenas séries
    Page<Media> findByIsFilmeFalse(Pageable pageable);

    // Busca por título (case insensitive)
    Page<Media> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // Top 10 mais curtidas
    @Query("SELECT m FROM Media m LEFT JOIN m.likes l GROUP BY m ORDER BY COUNT(l) DESC")
    List<Media> findTop10ByOrderByLikesDesc();

    long countByIsFilmeTrue();

    long countByIsFilmeFalse();

    @Query("SELECT AVG(m.avaliacao) FROM Media m")
    Double getAverageRating();

    @Query("SELECT m.title FROM Media m LEFT JOIN m.likes l GROUP BY m.title ORDER BY COUNT(l) DESC")
    String findMostLikedMediaTitle();

    @Query("SELECT m.categoria FROM Media m GROUP BY m.categoria ORDER BY COUNT(m) DESC")
    String findMostPopularCategory();
}