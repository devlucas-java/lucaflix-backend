package com.lucaflix.repository;

import com.lucaflix.model.Filme;
import com.lucaflix.model.Like;
import com.lucaflix.model.Serie;
import com.lucaflix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // Buscar like específico por usuário e filme
    Optional<Like> findByUserAndFilme(User user, Filme filme);

    // Buscar like específico por usuário e série
    Optional<Like> findByUserAndSerie(User user, Serie serie);

    // Buscar likes por IDs de usuário
    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.filme.id = :filmeId")
    Optional<Like> findByUserIdAndFilmeId(@Param("userId") UUID userId, @Param("filmeId") Long filmeId);

    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.serie.id = :serieId")
    Optional<Like> findByUserIdAndSerieId(@Param("userId") UUID userId, @Param("serieId") Long serieId);

    // Verificar se usuário já curtiu o filme
    boolean existsByUserAndFilme(User user, Filme filme);

    // Verificar se usuário já curtiu a série
    boolean existsByUserAndSerie(User user, Serie serie);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Like l WHERE l.user.id = :userId AND l.filme.id = :filmeId")
    boolean existsByUserIdAndFilmeId(@Param("userId") UUID userId, @Param("filmeId") Long filmeId);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Like l WHERE l.user.id = :userId AND l.serie.id = :serieId")
    boolean existsByUserIdAndSerieId(@Param("userId") UUID userId, @Param("serieId") Long serieId);

    // Contar total de likes por filme
    @Query("SELECT COUNT(l) FROM Like l WHERE l.filme = :filme")
    long countByFilme(@Param("filme") Filme filme);

    // Contar total de likes por série
    @Query("SELECT COUNT(l) FROM Like l WHERE l.serie = :serie")
    long countBySerie(@Param("serie") Serie serie);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.filme.id = :filmeId")
    long countByFilmeId(@Param("filmeId") Long filmeId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.serie.id = :serieId")
    long countBySerieId(@Param("serieId") Long serieId);

    // Buscar todos os likes de um usuário
    List<Like> findByUser(User user);

    @Query("SELECT l FROM Like l WHERE l.user.id = :userId")
    List<Like> findByUserId(@Param("userId") UUID userId);

    // Buscar todos os likes de um usuário ordenados por data
    List<Like> findByUserOrderByDataLikeDesc(User user);

    @Query("SELECT l FROM Like l WHERE l.user.id = :userId ORDER BY l.dataLike DESC")
    List<Like> findByUserIdOrderByDataLikeDesc(@Param("userId") UUID userId);

    // Buscar likes de filmes por usuário
    @Query("SELECT l FROM Like l WHERE l.user = :user AND l.filme IS NOT NULL ORDER BY l.dataLike DESC")
    List<Like> findFilmeLikesByUser(@Param("user") User user);

    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.filme IS NOT NULL ORDER BY l.dataLike DESC")
    List<Like> findFilmeLikesByUserId(@Param("userId") UUID userId);

    // Buscar likes de séries por usuário
    @Query("SELECT l FROM Like l WHERE l.user = :user AND l.serie IS NOT NULL ORDER BY l.dataLike DESC")
    List<Like> findSerieLikesByUser(@Param("user") User user);

    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.serie IS NOT NULL ORDER BY l.dataLike DESC")
    List<Like> findSerieLikesByUserId(@Param("userId") UUID userId);

    // Buscar todos os likes de um filme
    List<Like> findByFilme(Filme filme);

    @Query("SELECT l FROM Like l WHERE l.filme.id = :filmeId")
    List<Like> findByFilmeId(@Param("filmeId") Long filmeId);

    // Buscar todos os likes de uma série
    List<Like> findBySerie(Serie serie);

    @Query("SELECT l FROM Like l WHERE l.serie.id = :serieId")
    List<Like> findBySerieId(@Param("serieId") Long serieId);

    // Contar likes de filmes de um usuário
    @Query("SELECT COUNT(l) FROM Like l WHERE l.user = :user AND l.filme IS NOT NULL")
    long countFilmeLikesByUser(@Param("user") User user);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.user.id = :userId AND l.filme IS NOT NULL")
    long countFilmeLikesByUserId(@Param("userId") UUID userId);

    // Contar likes de séries de um usuário
    @Query("SELECT COUNT(l) FROM Like l WHERE l.user = :user AND l.serie IS NOT NULL")
    long countSerieLikesByUser(@Param("user") User user);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.user.id = :userId AND l.serie IS NOT NULL")
    long countSerieLikesByUserId(@Param("userId") UUID userId);

    // Deletar todos os likes de um usuário
    @Modifying
    @Query("DELETE FROM Like l WHERE l.user = :user")
    void deleteByUser(@Param("user") User user);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);

    // Deletar todos os likes de um filme
    @Modifying
    @Query("DELETE FROM Like l WHERE l.filme = :filme")
    void deleteByFilme(@Param("filme") Filme filme);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.filme.id = :filmeId")
    void deleteByFilmeId(@Param("filmeId") Long filmeId);

    // Deletar todos os likes de uma série
    @Modifying
    @Query("DELETE FROM Like l WHERE l.serie = :serie")
    void deleteBySerie(@Param("serie") Serie serie);

    @Modifying
    @Query("DELETE FROM Like l WHERE l.serie.id = :serieId")
    void deleteBySerieId(@Param("serieId") Long serieId);

    // Buscar filmes mais curtidos
    @Query("SELECT l.filme FROM Like l WHERE l.filme IS NOT NULL " +
            "GROUP BY l.filme " +
            "ORDER BY COUNT(l) DESC")
    List<Filme> findMostLikedFilmes();

    // Buscar séries mais curtidas
    @Query("SELECT l.serie FROM Like l WHERE l.serie IS NOT NULL " +
            "GROUP BY l.serie " +
            "ORDER BY COUNT(l) DESC")
    List<Serie> findMostLikedSeries();
}