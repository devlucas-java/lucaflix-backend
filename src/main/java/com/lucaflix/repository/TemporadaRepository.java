package com.lucaflix.repository;

import com.lucaflix.model.Serie;
import com.lucaflix.model.Temporada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemporadaRepository extends JpaRepository<Temporada, Long> {

    // Buscar temporadas de uma série específica
    List<Temporada> findBySerieOrderByNumeroTemporadaAsc(Serie serie);

    Page<Temporada> findBySerieOrderByNumeroTemporadaAsc(Serie serie, Pageable pageable);

    // Buscar temporada específica de uma série
    Optional<Temporada> findBySerieAndNumeroTemporada(Serie serie, Integer numeroTemporada);

    // Verificar se existe temporada com número específico para uma série
    boolean existsBySerieAndNumeroTemporada(Serie serie, Integer numeroTemporada);

    // Contar temporadas de uma série
    long countBySerie(Serie serie);

    // Buscar temporadas por série ID
    @Query("SELECT t FROM Temporada t WHERE t.serie.id = :serieId ORDER BY t.numeroTemporada ASC")
    List<Temporada> findBySerieIdOrderByNumeroTemporadaAsc(@Param("serieId") Long serieId);

    // Buscar temporada por série ID e número
    @Query("SELECT t FROM Temporada t WHERE t.serie.id = :serieId AND t.numeroTemporada = :numeroTemporada")
    Optional<Temporada> findBySerieIdAndNumeroTemporada(
            @Param("serieId") Long serieId,
            @Param("numeroTemporada") Integer numeroTemporada
    );

    // Estatísticas
    @Query("SELECT COUNT(t) FROM Temporada t")
    long countTotalTemporadas();

    @Query("SELECT AVG(t.totalEpisodios) FROM Temporada t WHERE t.totalEpisodios > 0")
    Double getAverageEpisodesPerSeason();

    // Buscar próximo número de temporada disponível para uma série
    @Query("SELECT COALESCE(MAX(t.numeroTemporada), 0) + 1 FROM Temporada t WHERE t.serie = :serie")
    Integer getNextSeasonNumber(@Param("serie") Serie serie);

    // Deletar todas as temporadas de uma série
    void deleteBySerie(Serie serie);
}