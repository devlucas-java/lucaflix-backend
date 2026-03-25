package com.lucaflix.dto.mapper;


import com.lucaflix.dto.response.movie.MovieCompleteDTO;
import com.lucaflix.dto.response.movie.MovieSimpleDTO;
import com.lucaflix.model.Movie;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    MovieCompleteDTO toComplete(Movie movie);

    MovieSimpleDTO toSimple(Movie movie);
}
