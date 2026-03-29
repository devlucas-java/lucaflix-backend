package com.lucaflix.repository;

import com.lucaflix.model.Movie;
import com.lucaflix.model.enums.Categories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID>, JpaSpecificationExecutor<Movie> {

    @Query("""
                SELECT DISTINCT m FROM Movie m
                JOIN m.categories c
                WHERE c IN :categories
                AND m.id <> :excludeId
                ORDER BY m.rating DESC
            """)
    Page<Movie> findSimilarMovie(
            @Param("categories") List<Categories> categories,
            @Param("excludeId") UUID excludeId,
            Pageable pageable
    );
    @Query("SELECT AVG(a.rating) FROM Movie a")
    Double getAverageRating();
}