package com.lucaflix.repository;

import com.lucaflix.dto.response.serie.SeasonDTO;
import com.lucaflix.model.Series;
import com.lucaflix.model.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {

    long countBySeries(Series series);
}