package com.lucaflix.dto.mapper;

import com.lucaflix.dto.response.anime.AnimeCompleteDTO;
import com.lucaflix.dto.response.anime.AnimeSimpleDTO;
import com.lucaflix.model.Anime;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnimeMapper {

    AnimeSimpleDTO animeSimpleDTO(Anime anime);

    AnimeCompleteDTO animeCompleteDTO(Anime anime);
}
