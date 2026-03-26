package com.lucaflix.dto.mapper;

import com.lucaflix.dto.response.serie.EpisodeDTO;
import com.lucaflix.dto.response.serie.SeasonDTO;
import com.lucaflix.model.Episode;
import com.lucaflix.model.Season;
import com.lucaflix.repository.EpisodeRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SeasonMapper {

    private final EpisodeRepository episodeRepository;
    private final EpisodeMapper episodeMapper;

    public SeasonDTO toDTO(Season season) {

        SeasonDTO dto = new SeasonDTO();
        dto.setId(season.getId());
        dto.setNumberSeason(season.getNumberSeason());
        dto.setDateRegistered(season.getDateRegistered());
        dto.setYearRelease(season.getYearRelease());

        List<Episode> ep = episodeRepository.findBySeasonOrderByIdAsc(season);

        List<EpisodeDTO> episodeDTOS = ep.stream().map(episodeMapper::toDTO).collect(Collectors.toList());
        dto.setEpisodes(episodeDTOS);

        return dto;
    }

    ;

}
