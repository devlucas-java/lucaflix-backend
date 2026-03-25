package com.lucaflix.dto.mapper;


import com.lucaflix.dto.response.serie.SerieCompleteDTO;
import com.lucaflix.dto.response.serie.SerieSimpleDTO;
import com.lucaflix.model.User;

public class SeriesMapperHelper {


    public SerieCompleteDTO helperToComplete(SerieCompleteDTO serie, User user) {

        return serie;
    }

    public SerieSimpleDTO helperToSimple(SerieSimpleDTO serie, User user) {

        return serie;
    }
}