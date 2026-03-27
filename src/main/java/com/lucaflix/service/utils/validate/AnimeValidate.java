package com.lucaflix.service.utils.validate;

import com.lucaflix.dto.request.anime.UpdateAnimeDTO;
import com.lucaflix.model.Anime;

public class AnimeValidate {

    public Anime validUpdate(UpdateAnimeDTO dto, Anime anime) {

        if (dto.getTitle() != null) {
            anime.setTitle(dto.getTitle().trim());
        }

        if (dto.getYearRealese() != null) {
            anime.setYearRealese(dto.getYearRealese());
        }

        if (dto.getTmdbId() != null) {
            anime.setTmdbId(dto.getTmdbId());
        }

        if (dto.getImdbId() != null) {
            anime.setImdbId(dto.getImdbId());
        }

        if (dto.getCountryOrigin() != null) {
            anime.setCountryOrigin(dto.getCountryOrigin());
        }

        if (dto.getSynopsis() != null) {
            anime.setSynopsis(dto.getSynopsis());
        }

        if (dto.getMinAge() != null) {
            anime.setMinAge(dto.getMinAge());
        }

        if (dto.getRating() != null) {
            anime.setRating(dto.getRating());
        }

        if (dto.getEmbed1() != null) {
            anime.setEmbed1(dto.getEmbed1());
        }

        if (dto.getEmbed2() != null) {
            anime.setEmbed2(dto.getEmbed2());
        }

        if (dto.getTrailer() != null) {
            anime.setTrailer(dto.getTrailer());
        }

        if (dto.getPosterURL1() != null) {
            anime.setPosterURL1(dto.getPosterURL1());
        }

        if (dto.getPosterURL2() != null) {
            anime.setPosterURL2(dto.getPosterURL2());
        }

        if (dto.getBackdropURL1() != null) {
            anime.setBackdropURL1(dto.getBackdropURL1());
        }

        if (dto.getBackdropURL2() != null) {
            anime.setBackdropURL2(dto.getBackdropURL2());
        }

        if (dto.getBackdropURL3() != null) {
            anime.setBackdropURL3(dto.getBackdropURL3());
        }

        if (dto.getBackdropURL4() != null) {
            anime.setBackdropURL4(dto.getBackdropURL4());
        }

        if (dto.getLogoURL1() != null) {
            anime.setLogoURL1(dto.getLogoURL1());
        }

        if (dto.getLogoURL2() != null) {
            anime.setLogoURL2(dto.getLogoURL2());
        }

        if (dto.getTotalSeason() != null) {
            anime.setTotalSeason(dto.getTotalSeason());
        }

        if (dto.getTotalEpisodes() != null) {
            anime.setTotalEpisodes(dto.getTotalEpisodes());
        }

        if (dto.getCategories() != null) {
            anime.setCategories(
                    dto.getCategories()
                            .stream()
                            .distinct()
                            .toList()
            );
        }

        return anime;
    }
}