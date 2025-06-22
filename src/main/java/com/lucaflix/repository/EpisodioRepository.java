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

    // Verificar se existe episódio com número específico em uma temporada
    boolean existsByTemporadaAndNumeroEpisodio(Temporada temporada, Integer numeroEpisodio);

    // Contar episódios de uma temporada
    long countByTemporada(Temporada temporada);

    // Buscar episódios de uma série específica
    @Query("SELECT e FROM Episodio e WHERE e.serie = :serie ORDER BY e.temporada.numeroTemporada ASC, e.numeroEpisodio ASC")
    List<Episodio> findBySerieOrderByTemporadaAndEpisodio(@Param("serie") Serie serie);

    // Contar episódios de uma série
    long countBySerie(Serie serie);

}