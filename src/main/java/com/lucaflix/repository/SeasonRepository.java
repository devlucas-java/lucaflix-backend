package com.lucaflix.repository;

import com.lucaflix.dto.response.serie.SeasonDTO;
import com.lucaflix.model.Series;
import com.lucaflix.model.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {

    // Buscar temporadas de uma série específica
    List<Season> findBySerieOrderByNumeroTemporadaAsc(Series series);

    // Verificar se existe temporada com número específico para uma série
    boolean existsBySerieAndNumeroTemporada(Series series, Integer numeroTemporada);

    long countBySeries(Series series);
}