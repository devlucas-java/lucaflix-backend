package com.lucaflix.repository;

import com.lucaflix.model.Serie;
import com.lucaflix.model.Temporada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemporadaRepository extends JpaRepository<Temporada, Long> {

    // Buscar temporadas por série ordenadas por número
    List<Temporada> findBySerieOrderByNumeroTemporadaAsc(Serie serie);

    @Query("SELECT t FROM Temporada t WHERE t.serie.id = :serieId ORDER BY t.numeroTemporada ASC")
    List<Temporada> findBySerieIdOrderByNumeroTemporadaAsc(@Param("serieId") Long serieId);

    // Buscar temporada específica por série e número
    Optional<Temporada> findBySerieAndNumeroTemporada(Serie serie, Short numeroTemporada);

    @Query("SELECT t FROM Temporada t WHERE t.serie.id = :serieId AND t.numeroTemporada = :numeroTemporada")
    Optional<Temporada> findBySerieIdAndNumeroTemporada(@Param("serieId") Long serieId, @Param("numeroTemporada") Short numeroTemporada);

    // Buscar temporada com episódios carregados
    @Query("SELECT DISTINCT t FROM Temporada t LEFT JOIN FETCH t.episodios e WHERE t.id = :id ORDER BY e.numeroEpisodio ASC")
    Optional<Temporada> findByIdWithEpisodios(@Param("id") Long id);

    // Contar temporadas por série
    long countBySerie(Serie serie);

    @Query("SELECT COUNT(t) FROM Temporada t WHERE t.serie.id = :serieId")
    long countBySerieId(@Param("serieId") Long serieId);

    // Buscar primeira temporada de uma série
    @Query("SELECT t FROM Temporada t WHERE t.serie = :serie ORDER BY t.numeroTemporada ASC")
    Optional<Temporada> findFirstTemporadaBySerie(@Param("serie") Serie serie);

    @Query("SELECT t FROM Temporada t WHERE t.serie.id = :serieId ORDER BY t.numeroTemporada ASC")
    Optional<Temporada> findFirstTemporadaBySerieId(@Param("serieId") Long serieId);

    // Buscar última temporada de uma série
    @Query("SELECT t FROM Temporada t WHERE t.serie = :serie ORDER BY t.numeroTemporada DESC")
    Optional<Temporada> findLastTemporadaBySerie(@Param("serie") Serie serie);

    @Query("SELECT t FROM Temporada t WHERE t.serie.id = :serieId ORDER BY t.numeroTemporada DESC")
    Optional<Temporada> findLastTemporadaBySerieId(@Param("serieId") Long serieId);

    // Buscar próxima temporada
    @Query("SELECT t FROM Temporada t WHERE t.serie.id = :serieId AND t.numeroTemporada > :numeroAtual ORDER BY t.numeroTemporada ASC")
    Optional<Temporada> findNextTemporada(@Param("serieId") Long serieId, @Param("numeroAtual") Short numeroAtual);

    // Buscar temporada anterior
    @Query("SELECT t FROM Temporada t WHERE t.serie.id = :serieId AND t.numeroTemporada < :numeroAtual ORDER BY t.numeroTemporada DESC")
    Optional<Temporada> findPreviousTemporada(@Param("serieId") Long serieId, @Param("numeroAtual") Short numeroAtual);

    // Verificar se existe temporada com número específico na série
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Temporada t WHERE t.serie = :serie AND t.numeroTemporada = :numeroTemporada")
    boolean existsBySerieAndNumeroTemporada(@Param("serie") Serie serie, @Param("numeroTemporada") Short numeroTemporada);

    // Buscar temporadas com título não nulo
    @Query("SELECT t FROM Temporada t WHERE t.titulo IS NOT NULL AND LENGTH(TRIM(t.titulo)) > 0 ORDER BY t.serie.id, t.numeroTemporada")
    List<Temporada> findTemporadasWithTitulo();

    // Buscar temporadas por título (busca parcial)
    @Query("SELECT t FROM Temporada t WHERE LOWER(t.titulo) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY t.serie.title, t.numeroTemporada")
    List<Temporada> findByTituloContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    // Buscar temporadas com episódios
    @Query("SELECT DISTINCT t FROM Temporada t WHERE SIZE(t.episodios) > 0 ORDER BY t.serie.id, t.numeroTemporada")
    List<Temporada> findTemporadasWithEpisodios();

    // Buscar temporadas sem episódios
    @Query("SELECT t FROM Temporada t WHERE SIZE(t.episodios) = 0 ORDER BY t.serie.id, t.numeroTemporada")
    List<Temporada> findTemporadasWithoutEpisodios();

    // Deletar todas as temporadas de uma série
    void deleteBySerie(Serie serie);

    @Query("DELETE FROM Temporada t WHERE t.serie.id = :serieId")
    void deleteBySerieId(@Param("serieId") Long serieId);
}