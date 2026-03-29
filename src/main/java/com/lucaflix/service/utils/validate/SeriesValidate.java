package com.lucaflix.service.utils.validate;

import com.lucaflix.dto.request.serie.UpdateSerieDTO;
import com.lucaflix.model.Series;
import org.springframework.stereotype.Component;

@Component
public class SeriesValidate {
    public Series validUpdate(UpdateSerieDTO dto, Series series) {

        if (dto.getTitle() != null) {
            series.setTitle(dto.getTitle().trim());
        }

        if (dto.getYearRelease() != null) {
            series.setYearRelease(dto.getYearRelease());
        }

        if (dto.getTmdbId() != null) {
            series.setTmdbId(dto.getTmdbId());
        }

        if (dto.getImdbId() != null) {
            series.setImdbId(dto.getImdbId());
        }

        if (dto.getCountryOrigin() != null) {
            series.setCountryOrigin(dto.getCountryOrigin());
        }

        if (dto.getSynopsis() != null) {
            series.setSynopsis(dto.getSynopsis());
        }

        if (dto.getMinAge() != null) {
            series.setMinAge(dto.getMinAge());
        }

        if (dto.getRating() != null) {
            series.setRating(dto.getRating());
        }

        if (dto.getTrailer() != null) {
            series.setTrailer(dto.getTrailer());
        }

        if (dto.getPosterURL1() != null) {
            series.setPosterURL1(dto.getPosterURL1());
        }

        if (dto.getPosterURL2() != null) {
            series.setPosterURL2(dto.getPosterURL2());
        }

        if (dto.getBackdropURL1() != null) {
            series.setBackdropURL1(dto.getBackdropURL1());
        }

        if (dto.getBackdropURL2() != null) {
            series.setBackdropURL2(dto.getBackdropURL2());
        }

        if (dto.getBackdropURL3() != null) {
            series.setBackdropURL3(dto.getBackdropURL3());
        }

        if (dto.getBackdropURL4() != null) {
            series.setBackdropURL4(dto.getBackdropURL4());
        }

        if (dto.getLogoURL1() != null) {
            series.setLogoURL1(dto.getLogoURL1());
        }

        if (dto.getLogoURL2() != null) {
            series.setLogoURL2(dto.getLogoURL2());
        }

        if (dto.getCategories() != null) {
            series.setCategories(
                    dto.getCategories().stream().distinct().toList()
            );
        }

        return series;
    }
}