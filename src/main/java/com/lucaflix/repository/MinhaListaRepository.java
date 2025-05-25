package com.lucaflix.repository;

import com.lucaflix.model.Filme;
import com.lucaflix.model.MinhaLista;
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
public interface MinhaListaRepository extends JpaRepository<MinhaLista, Long> {

    Optional<MinhaLista> findByUserAndFilme(User user, Filme filme);

    Optional<MinhaLista> findByUserAndSerie(User user, Serie serie);

    @Query("SELECT ml FROM MinhaLista ml WHERE ml.user.id = :userId AND ml.filme.id = :filmeId")
    Optional<MinhaLista> findByUserIdAndFilmeId(@Param("userId") UUID userId, @Param("filmeId") Long filmeId);

    @Query("SELECT ml FROM MinhaLista ml WHERE ml.user.id = :userId AND ml.serie.id = :serieId")
    Optional<MinhaLista> findByUserIdAndSerieId(@Param("userId") UUID userId, @Param("serieId") Long serieId);

    List<MinhaLista> findByUserOrderByDataAdicaoDesc(User user);

    @Query("SELECT ml FROM MinhaLista ml WHERE ml.user.id = :userId ORDER BY ml.dataAdicao DESC")
    List<MinhaLista> findByUserIdOrderByDataAdicaoDesc(@Param("userId") UUID userId);

    @Query("SELECT ml FROM MinhaLista ml WHERE ml.user = :user AND ml.filme IS NOT NULL AND ml.assistido = true ORDER BY ml.dataUltimaVisualizacao DESC")
    List<MinhaLista> findByUserAndFilmeIsNotNullAndAssistidoTrueOrderByDataUltimaVisualizacaoDesc(@Param("user") User user);

    @Query("SELECT ml FROM MinhaLista ml WHERE ml.user = :user AND ml.serie IS NOT NULL ORDER BY ml.dataUltimaVisualizacao DESC")
    List<MinhaLista> findByUserAndSerieIsNotNullOrderByDataUltimaVisualizacaoDesc(@Param("user") User user);

    @Query("SELECT ml FROM MinhaLista ml WHERE ml.user = :user AND ml.filme IS NOT NULL ORDER BY ml.dataAdicao DESC")
    List<MinhaLista> findFilmesByUser(@Param("user") User user);

    @Query("SELECT ml FROM MinhaLista ml WHERE ml.user = :user AND ml.serie IS NOT NULL ORDER BY ml.dataAdicao DESC")
    List<MinhaLista> findSeriesByUser(@Param("user") User user);

    @Query("SELECT ml FROM MinhaLista ml WHERE ml.user = :user AND ml.filme IS NOT NULL AND ml.assistido = false ORDER BY ml.dataAdicao DESC")
    List<MinhaLista> findUnwatchedFilmesByUser(@Param("user") User user);

    @Query("SELECT COUNT(ml) FROM MinhaLista ml WHERE ml.user = :user AND ml.filme IS NOT NULL")
    long countFilmesByUser(@Param("user") User user);

    @Query("SELECT COUNT(ml) FROM MinhaLista ml WHERE ml.user = :user AND ml.serie IS NOT NULL")
    long countSeriesByUser(@Param("user") User user);

    boolean existsByUserAndFilme(User user, Filme filme);

    boolean existsByUserAndSerie(User user, Serie serie);

    @Modifying
    @Query("DELETE FROM MinhaLista ml WHERE ml.user = :user")
    void deleteByUser(@Param("user") User user);

    @Modifying
    @Query("DELETE FROM MinhaLista ml WHERE ml.filme = :filme")
    void deleteByFilme(@Param("filme") Filme filme);

    @Modifying
    @Query("DELETE FROM MinhaLista ml WHERE ml.serie = :serie")
    void deleteBySerie(@Param("serie") Serie serie);
}