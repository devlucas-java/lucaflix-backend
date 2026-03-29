package com.lucaflix.dto.mapper;

import com.lucaflix.dto.response.serie.EpisodeDTO;
import com.lucaflix.dto.response.serie.SeasonDTO;
import com.lucaflix.model.Season;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class SeasonMapper {

    private final EpisodeMapper episodeMapper;

    public SeasonDTO toDTO(Season season) {

        SeasonDTO dto = new SeasonDTO();

        dto.setId(season.getId());
        dto.setNumberSeason(season.getNumberSeason());
        dto.setYearRelease(season.getYearRelease());
        dto.setDateRegistered(season.getDateRegistered());

        List<EpisodeDTO> episodes = season.getEpisodes()
                .stream()
                .sorted((e1, e2) -> e1.getNumberEpisode().compareTo(e2.getNumberEpisode()))
                .map(episodeMapper::toDTO)
                .toList();

        dto.setEpisodes(episodes);
        dto.setTotalEpisodes(episodes.size());

        return dto;
    }
}