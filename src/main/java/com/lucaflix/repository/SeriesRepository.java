package com.lucaflix.repository;

import com.lucaflix.model.Series;
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
public interface SeriesRepository extends JpaRepository<Series, UUID>, JpaSpecificationExecutor<Series> {

    @Query("""
                SELECT DISTINCT s FROM Series s
                JOIN s.categories c
                WHERE c IN :categories
                AND s.id <> :excludeId
                ORDER BY s.rating DESC
            """)
    Page<Series> findSimilarSeries(
            @Param("categories") List<Categories> categories,
            @Param("excludeId") UUID excludeId,
            Pageable pageable
    );
    Double getAverageRating();
}