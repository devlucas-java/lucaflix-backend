package com.lucaflix.repository;

import com.lucaflix.model.*;
import com.lucaflix.model.enums.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MyListItemRepository extends JpaRepository<MyListItem, Long> {


    boolean existsByUserAndMovie(User user, Movie movie);
    void deleteByMovie(Movie movie);

    boolean existsByUserAndSeries(User user, Series series);
    void deleteBySeries(Series series);

    boolean existsByUserAndAnime(User user, Anime anime);
    void deleteByAnime(Anime anime);

    Page<MyListItem> findByUser(User user, Pageable pageable);

    @Modifying
    @Query("DELETE FROM MyListItem ml WHERE ml.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);

    long countBySeries(Series series);

    long countByMovie(Movie movie);

    long countByAnime(Anime anime);

    boolean existsByUserAndContentIdAndType(User user, UUID contentId, MediaType type);

    Optional<MyListItem> findByUserAndContentIdAndType(User user, UUID contentId, MediaType type);
}