package com.lucaflix.dto.mapper;


import com.lucaflix.dto.response.serie.SerieCompleteDTO;
import com.lucaflix.dto.response.serie.SerieSimpleDTO;
import com.lucaflix.model.Series;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SeriesMapper {

    SerieCompleteDTO toComplete(Series series);

    SerieSimpleDTO toSimple(Series series);
}
