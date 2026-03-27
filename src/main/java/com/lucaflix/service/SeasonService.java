package com.lucaflix.service;

import com.lucaflix.dto.mapper.EpisodeMapper;
import com.lucaflix.dto.mapper.SeasonMapper;
import com.lucaflix.dto.request.serie.CreateSeasonDTO;
import com.lucaflix.dto.request.serie.UpdateSeasonDTO;
import com.lucaflix.dto.response.serie.EpisodeDTO;
import com.lucaflix.dto.response.serie.SeasonDTO;
import com.lucaflix.model.Episode;
import com.lucaflix.model.Season;
import com.lucaflix.model.Series;
import com.lucaflix.repository.SeasonRepository;
import com.lucaflix.repository.SeriesRepository;
import com.lucaflix.service.utils.sanitize.SanitizeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeasonService {

    private final SeasonRepository seasonRepository;
    private final SeriesRepository seriesRepository;
    private final SeasonMapper seasonMapper;
    private final EpisodeMapper episodeMapper;


    public SeasonDTO getSeason(Long id) {
        Season season = seasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Season not found"));
        return seasonMapper.toDTO(season);
    }

    @Transactional
    public SeasonDTO createSeason(CreateSeasonDTO createDTO, UUID id) {
        Series series = seriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Series not found"));

        SanitizeUtils.sanitizeStrings(createDTO);
        createDTO.getEpisodes().forEach(SanitizeUtils::sanitizeStrings);

        Season season = new Season();
        season.setSeries(series);
        season.setNumberSeason(createDTO.getNumberSeason());
        season.setYearRelease(createDTO.getYearRelease());

        Set<Episode> episodes = createDTO.getEpisodes().stream().map(epDTO -> {

            Episode ep = episodeMapper.toEntity(epDTO);
            ep.setSeason(season);

            return ep;

        }).collect(Collectors.toSet());

        season.setEpisodes(episodes);
        season.setTotalEpisodes(episodes.size());
        seasonRepository.save(season);

        return seasonMapper.toDTO(season);
    }

    @Transactional
    public SeasonDTO updateSeason(UpdateSeasonDTO updateDTO, Long id) {
        Season season = seasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Season not found"));

        SanitizeUtils.sanitizeStrings(updateDTO);
        season.setNumberSeason(updateDTO.getNumberSeason());
        season.setYearRelease(updateDTO.getYearRelease());

        return seasonMapper.toDTO(season);
    }

    @Transactional
    public void deleteSeason(Long id) {
        Season season = seasonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Season not found"));

        seasonRepository.delete(season);
    }
}

