package com.lucaflix.repository;

import com.lucaflix.model.Episode;
import com.lucaflix.model.Series;
import com.lucaflix.model.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long> {

    // Buscar episódios de uma temporada específica
    List<Episode> findByTemporadaOrderByNumeroEpisodioAsc(Season season);

    // Verificar se existe episódio com número específico em uma temporada
    boolean existsByTemporadaAndNumeroEpisodio(Season season, Integer numeroEpisodio);

    // Contar episódios de uma temporada
    long countByTemporada(Season season);

    List<Episode> findBySeasonOrderByIdAsc(Season season);
}