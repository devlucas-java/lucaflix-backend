package com.lucaflix.dto.mapper;

import com.lucaflix.dto.response.serie.SerieCompleteDTO;
import com.lucaflix.dto.response.serie.SerieSimpleDTO;
import com.lucaflix.model.Series;
import com.lucaflix.model.User;
import com.lucaflix.repository.LikeRepository;
import com.lucaflix.repository.MyListRepository;
import com.lucaflix.repository.SeasonRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SeriesMapper {

    private final SeasonRepository seasonRepository;
    private final LikeRepository likeRepository;
    private final MyListRepository myListRepository;

    public SerieCompleteDTO toComplete(Series series, User user) {

        SerieCompleteDTO dto = new SerieCompleteDTO();
        if (user == null) {
            dto.setUserLiked(false);
            dto.setInMyList(false);
        } else {
            boolean like = likeRepository.existsByUserAndSeries(user, series);
            boolean myList = myListRepository.existsByUserAndSerie(user, series);

            dto.setUserLiked(like);
            dto.setInMyList(myList);
        }


        dto.setId(series.getId());
        dto.setTitle(series.getTitle());
        dto.setYearRelease(series.getYearRelease());
        dto.setTmdbId(series.getTmdbId());
        dto.setImdbId(series.getImdbId());
        dto.setCountryOrigin(series.getCountryOrigin());
        dto.setSynopsis(series.getSynopsis());
        dto.setDateRegistered(series.getDateRegistered().toLocalDate());
        dto.setCategories(series.getCategories());
        dto.setMinAge(series.getMinAge());
        dto.setRating(series.getRating());

        dto.setTrailer(series.getTrailer());
        dto.setPosterURL1(series.getPosterURL1());
        dto.setPosterURL2(series.getPosterURL2());
        dto.setBackdropURL1(series.getBackdropURL1());
        dto.setBackdropURL2(series.getBackdropURL2());
        dto.setBackdropURL3(series.getBackdropURL3());
        dto.setBackdropURL4(series.getBackdropURL4());
        dto.setLogoURL1(series.getLogoURL1());
        dto.setLogoURL2(series.getLogoURL2());

        dto.setTotalSeason(seasonRepository.countBySeries(series));
        dto.setTotalLikes((long) series.getLikes().size());

        return dto;
    }

    public SerieSimpleDTO toSimple(Series series, User user) {

        SerieSimpleDTO dto = new SerieSimpleDTO();
        if (user == null) {
            dto.setUserLiked(false);
            dto.setInMyList(false);
        } else {
            boolean like = likeRepository.existsByUserAndSeries(user, series);
            boolean myList = myListRepository.existsByUserAndSerie(user, series);

            dto.setUserLiked(like);
            dto.setInMyList(myList);
        }

        dto.setId(series.getId());
        dto.setTitle(series.getTitle());
        dto.setYearRelease(series.getYearRelease());
        dto.setTmdbId(series.getTmdbId());
        dto.setImdbId(series.getImdbId());
        dto.setCountryOrigin(series.getCountryOrigin());
        dto.setCategories(series.getCategories());
        dto.setMinAge(series.getMinAge());
        dto.setRating(series.getRating());

        dto.setPosterURL1(series.getPosterURL1());
        dto.setPosterURL2(series.getPosterURL2());

        dto.setTotalSeason(seasonRepository.countBySeries(series));
        dto.setTotalLikes((long) series.getLikes().size());

        return dto;
    }
}