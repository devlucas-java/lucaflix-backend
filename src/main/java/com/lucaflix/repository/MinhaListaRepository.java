package com.lucaflix.repository;

import com.lucaflix.model.*;
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
public interface MinhaListaRepository extends JpaRepository<MyList, Long> {

    // Para filmes
    boolean existsByUserAndMovie(User user, Movie movie);
    Optional<MyList> findByUserAndMovie(User user, Movie movie);
    void deleteByMovie(Movie movie);

    // Para séries
    boolean existsByUserAndSerie(User user, Series series);
    Optional<MyList> findByUserAndSerie(User user, Series series);
    void deleteBySerie(Series series);

    // Para animes
    boolean existsByUserAndAnime(User user, Anime anime);
    Optional<MyList> findByUserAndAnime(User user, Anime anime);
    void deleteByAnime(Anime anime);

    // Busca toda a lista do usuário com paginação
    Page<MyList> findByUser(User user, Pageable pageable);

    // Busca apenas animes na lista do usuário
    @Query("SELECT ml FROM MinhaLista ml WHERE ml.user = :user AND ml.anime IS NOT NULL")
    Page<MyList> findAnimesByUser(@Param("user") User user, Pageable pageable);

    // Estatísticas
    @Query("SELECT COUNT(DISTINCT u.id) FROM MinhaLista ml JOIN ml.user u")
    long countDistinctUsers();

    @Query("SELECT COUNT(ml) FROM MinhaLista ml WHERE ml.movie IS NOT NULL")
    long countMovieItems();

    @Query("SELECT COUNT(ml) FROM MinhaLista ml WHERE ml.serie IS NOT NULL")
    long countSerieItems();

    @Query("SELECT COUNT(ml) FROM MinhaLista ml WHERE ml.anime IS NOT NULL")
    long countAnimeItems();

    @Modifying
    @Query("DELETE FROM MinhaLista ml WHERE ml.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);

    long countBySerie(Series series);

    long countByMovie(Movie movie);

    long countByAnime(Anime anime);
}