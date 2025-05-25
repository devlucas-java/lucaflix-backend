package com.lucaflix.repository;

import com.lucaflix.model.Episodio;
import com.lucaflix.model.Temporada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EpisodioRepository extends JpaRepository<Episodio, Long> {

    // Buscar episódios por temporada ordenados por número
    List<Episodio> findByTemporadaOrderByNumeroEpisodioAsc(Temporada temporada);

    @Query("SELECT e FROM Episodio e WHERE e.temporada.id = :temporadaId ORDER BY e.numeroEpisodio ASC")
    List<Episodio> findByTemporadaIdOrderByNumeroEpisodioAsc(@Param("temporadaId") Long temporadaId);

    // Buscar episódio específico por temporada e número
    Optional<Episodio> findByTemporadaAndNumeroEpisodio(Temporada temporada, Short numeroEpisodio);

    @Query("SELECT e FROM Episodio e WHERE e.temporada.id = :temporadaId AND e.numeroEpisodio = :numeroEpisodio")
    Optional<Episodio> findByTemporadaIdAndNumeroEpisodio(@Param("temporadaId") Long temporadaId, @Param("numeroEpisodio") Short numeroEpisodio);

    // Buscar todos os episódios de uma série
    @Query("SELECT e FROM Episodio e JOIN e.temporada t WHERE t.serie.id = :serieId ORDER BY t.numeroTemporada ASC, e.numeroEpisodio ASC")
    List<Episodio> findBySerieIdOrderByTemporadaAndEpisodio(@Param("serieId") Long serieId);

    // Contar episódios por temporada
    long countByTemporada(Temporada temporada);

    @Query("SELECT COUNT(e) FROM Episodio e WHERE e.temporada.id = :temporadaId")
    long countByTemporadaId(@Param("temporadaId") Long temporadaId);

    // Contar total de episódios de uma série
    @Query("SELECT COUNT(e) FROM Episodio e JOIN e.temporada t WHERE t.serie.id = :serieId")
    long countBySerieId(@Param("serieId") Long serieId);

    // Buscar episódios por título (busca parcial)
    @Query("SELECT e FROM Episodio e WHERE LOWER(e.titulo) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY e.titulo ASC")
    List<Episodio> findByTituloContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    // Buscar episódios com duração específica
    List<Episodio> findByDuracaoMinutos(Integer duracao);

    // Buscar episódios com duração maior que
    @Query("SELECT e FROM Episodio e WHERE e.duracaoMinutos > :minutos ORDER BY e.duracaoMinutos DESC")
    List<Episodio> findByDuracaoMinutosGreaterThan(@Param("minutos") Integer minutos);

    // Buscar episódios com embed URL não nulo
    @Query("SELECT e FROM Episodio e WHERE e.embedUrl IS NOT NULL AND LENGTH(TRIM(e.embedUrl)) > 0")
    List<Episodio> findEpisodiosByEmbedUrlNotNull();

    // Buscar episódios sem embed URL
    @Query("SELECT e FROM Episodio e WHERE e.embedUrl IS NULL OR LENGTH(TRIM(e.embedUrl)) = 0")
    List<Episodio> findEpisodiosByEmbedUrlNull();

    // Verificar se existe episódio com título específico na temporada
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Episodio e WHERE e.temporada = :temporada AND LOWER(TRIM(e.titulo)) = LOWER(TRIM(:titulo))")
    boolean existsByTemporadaAndTituloIgnoreCase(@Param("temporada") Temporada temporada, @Param("titulo") String titulo);

    // Buscar próximo episódio
    @Query("SELECT e FROM Episodio e WHERE e.temporada.id = :temporadaId AND e.numeroEpisodio > :numeroAtual ORDER BY e.numeroEpisodio ASC")
    Optional<Episodio> findNextEpisodio(@Param("temporadaId") Long temporadaId, @Param("numeroAtual") Short numeroAtual);

    // Buscar episódio anterior
    @Query("SELECT e FROM Episodio e WHERE e.temporada.id = :temporadaId AND e.numeroEpisodio < :numeroAtual ORDER BY e.numeroEpisodio DESC")
    Optional<Episodio> findPreviousEpisodio(@Param("temporadaId") Long temporadaId, @Param("numeroAtual") Short numeroAtual);

    // Buscar primeiro episódio da temporada
    @Query("SELECT e FROM Episodio e WHERE e.temporada.id = :temporadaId ORDER BY e.numeroEpisodio ASC")
    Optional<Episodio> findFirstEpisodioByTemporada(@Param("temporadaId") Long temporadaId);

    // Buscar último episódio da temporada
    @Query("SELECT e FROM Episodio e WHERE e.temporada.id = :temporadaId ORDER BY e.numeroEpisodio DESC")
    Optional<Episodio> findLastEpisodioByTemporada(@Param("temporadaId") Long temporadaId);

    // Deletar todos os episódios de uma temporada
    void deleteByTemporada(Temporada temporada);

    @Query("DELETE FROM Episodio e WHERE e.temporada.id = :temporadaId")
    void deleteByTemporadaId(@Param("temporadaId") Long temporadaId);
}