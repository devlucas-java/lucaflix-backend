package com.lucaflix.repository;

import com.lucaflix.model.Episodio;
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
public interface EpisodioRepository extends JpaRepository<Episodio, Long> {

    // Buscar episódios de uma temporada específica
    List<Episodio> findByTemporadaOrderByNumeroEpisodioAsc(Temporada temporada);

    Page<Episodio> findByTemporadaOrderByNumeroEpisodioAsc(Temporada temporada, Pageable pageable);

    // Buscar episódio específico
    Optional<Episodio> findByTemporadaAndNumeroEpisodio(Temporada temporada, Integer numeroEpisodio);

    // Verificar se existe episódio com número específico em uma temporada
    boolean existsByTemporadaAndNumeroEpisodio(Temporada temporada, Integer numeroEpisodio);

    // Contar episódios de uma temporada
    long countByTemporada(Temporada temporada);

    // Buscar episódios de uma série específica
    @Query("SELECT e FROM Episodio e WHERE e.serie = :serie ORDER BY e.temporada.numeroTemporada ASC, e.numeroEpisodio ASC")
    List<Episodio> findBySerieOrderByTemporadaAndEpisodio(@Param("serie") Serie serie);

    @Query("SELECT e FROM Episodio e WHERE e.serie = :serie ORDER BY e.temporada.numeroTemporada ASC, e.numeroEpisodio ASC")
    Page<Episodio> findBySerieOrderByTemporadaAndEpisodio(@Param("serie") Serie serie, Pageable pageable);

    // Buscar episódios por série ID
    @Query("SELECT e FROM Episodio e WHERE e.serie.id = :serieId ORDER BY e.temporada.numeroTemporada ASC, e.numeroEpisodio ASC")
    List<Episodio> findBySerieIdOrderByTemporadaAndEpisodio(@Param("serieId") Long serieId);

    // Buscar episódios por temporada ID
    @Query("SELECT e FROM Episodio e WHERE e.temporada.id = :temporadaId ORDER BY e.numeroEpisodio ASC")
    List<Episodio> findByTemporadaIdOrderByNumeroEpisodio(@Param("temporadaId") Long temporadaId);

    // Buscar episódio específico por IDs
    @Query("SELECT e FROM Episodio e WHERE e.serie.id = :serieId AND e.temporada.numeroTemporada = :numeroTemporada AND e.numeroEpisodio = :numeroEpisodio")
    Optional<Episodio> findBySerieIdAndTemporadaAndEpisodio(
            @Param("serieId") Long serieId,
            @Param("numeroTemporada") Integer numeroTemporada,
            @Param("numeroEpisodio") Integer numeroEpisodio
    );

    // Contar episódios de uma série
    long countBySerie(Serie serie);

    // Buscar próximo número de episódio disponível para uma temporada
    @Query("SELECT COALESCE(MAX(e.numeroEpisodio), 0) + 1 FROM Episodio e WHERE e.temporada = :temporada")
    Integer getNextEpisodeNumber(@Param("temporada") Temporada temporada);

    // Estatísticas
    @Query("SELECT COUNT(e) FROM Episodio e")
    long countTotalEpisodios();

    @Query("SELECT AVG(e.duracaoMinutos) FROM Episodio e WHERE e.duracaoMinutos > 0")
    Double getAverageEpisodeDuration();

    @Query("SELECT COUNT(e) FROM Episodio e WHERE e.embed1 IS NOT NULL OR e.embed2 IS NOT NULL")
    long countEpisodesWithVideo();

    // Buscar episódios recentes
    @Query("SELECT e FROM Episodio e ORDER BY e.dataCadastro DESC")
    Page<Episodio> findRecentEpisodes(Pageable pageable);

    // Deletar todos os episódios de uma série
    void deleteBySerie(Serie serie);

    // Deletar todos os episódios de uma temporada
    void deleteByTemporada(Temporada temporada);
}