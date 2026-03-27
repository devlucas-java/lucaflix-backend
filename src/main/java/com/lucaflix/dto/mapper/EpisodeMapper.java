package com.lucaflix.dto.mapper;

import com.lucaflix.dto.request.serie.CreateEpisodeDTO;
import com.lucaflix.dto.response.serie.EpisodeDTO;
import com.lucaflix.model.Episode;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EpisodeMapper {

    EpisodeDTO toDTO(Episode episode);

    Episode toEntity(CreateEpisodeDTO episodeDTO);
}
