package com.lucaflix.service.utils.validate;

import com.lucaflix.dto.request.movie.UpdateMovieDTO;
import com.lucaflix.model.Movie;

public class MovieValidate {
    public Movie validUpdate(UpdateMovieDTO dto, Movie movie) {

        if (dto.getTitle() != null) {
            movie.setTitle(dto.getTitle().trim());
        }

        if (dto.getYearRelease() != null) {
            movie.setYearRelease(dto.getYearRelease());
        }

        if (dto.getMinutesDuration() != null) {
            movie.setMinutesDuration(dto.getMinutesDuration());
        }

        if (dto.getSynopsis() != null) {
            movie.setSynopsis(dto.getSynopsis());
        }

        if (dto.getMinAge() != null) {
            movie.setMinAge(dto.getMinAge());
        }

        if (dto.getRating() != null) {
            movie.setRating(dto.getRating());
        }

        if (dto.getEmbed1() != null) {
            movie.setEmbed1(dto.getEmbed1());
        }

        if (dto.getEmbed2() != null) {
            movie.setEmbed2(dto.getEmbed2());
        }

        if (dto.getTrailer() != null) {
            movie.setTrailer(dto.getTrailer());
        }

        if (dto.getTmdbId() != null) {
            movie.setTmdbId(dto.getTmdbId());
        }

        if (dto.getImdbId() != null) {
            movie.setImdbId(dto.getImdbId());
        }

        if (dto.getCountryOrigin() != null) {
            movie.setCountryOrigin(dto.getCountryOrigin());
        }

        if (dto.getPosterURL1() != null) {
            movie.setPosterURL1(dto.getPosterURL1());
        }

        if (dto.getPosterURL2() != null) {
            movie.setPosterURL2(dto.getPosterURL2());
        }

        if (dto.getBackdropURL1() != null) {
            movie.setBackdropURL1(dto.getBackdropURL1());
        }

        if (dto.getBackdropURL2() != null) {
            movie.setBackdropURL2(dto.getBackdropURL2());
        }

        if (dto.getBackdropURL3() != null) {
            movie.setBackdropURL3(dto.getBackdropURL3());
        }

        if (dto.getBackdropURL4() != null) {
            movie.setBackdropURL4(dto.getBackdropURL4());
        }

        if (dto.getLogoURL1() != null) {
            movie.setLogoURL1(dto.getLogoURL1());
        }

        if (dto.getLogoURL2() != null) {
            movie.setLogoURL2(dto.getLogoURL2());
        }

        if (dto.getCategories() != null) {
            movie.setCategories(dto.getCategories().stream().distinct().toList());
        }
        return movie;
    }
}