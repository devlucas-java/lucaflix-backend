package com.lucaflix.repository;

import com.lucaflix.model.Anime;
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
public interface AnimeRepository extends JpaRepository<Anime, UUID>, JpaSpecificationExecutor<Anime> {


    @Query("""
                SELECT DISTINCT a FROM Anime a
                JOIN a.categories c
                WHERE c IN :categories
                AND a.id <> :excludeId
                ORDER BY a.rating DESC
            """)
    Page<Anime> findSimilarAnime(
            @Param("categories") List<Categories> categories,
            @Param("excludeId") UUID excludeId,
            Pageable pageable
    );

    @Query("SELECT AVG(a.rating) FROM Anime a")
    Double getAverageRating();
}