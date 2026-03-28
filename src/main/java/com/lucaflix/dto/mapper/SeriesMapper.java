package com.lucaflix.dto.mapper;

import com.lucaflix.dto.request.serie.CreateSerieDTO;
import com.lucaflix.dto.response.serie.SerieCompleteDTO;
import com.lucaflix.dto.response.serie.SerieSimpleDTO;
import com.lucaflix.model.Series;
import com.lucaflix.model.User;
import com.lucaflix.model.enums.MediaType;
import com.lucaflix.repository.LikeRepository;
import com.lucaflix.repository.MyListItemRepository;
import com.lucaflix.repository.SeasonRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SeriesMapper {

    private final SeasonRepository seasonRepository;
    private final LikeRepository likeRepository;
    private final MyListItemRepository myListItemRepository;

    public SerieCompleteDTO toComplete(Series series, User user) {

        SerieCompleteDTO dto = new SerieCompleteDTO();
        if (user == null) {
            dto.setUserLiked(false);
            dto.setInMyList(false);
        } else {
            boolean like = likeRepository.existsByUserAndSeries(user, series);
            boolean myList = myListItemRepository.existsByUserAndContentIdAndType(user, series.getId(), MediaType.SERIES);

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
        dto.setTotalLikes(likeRepository.countBySeries(series));

        return dto;
    }

    public SerieSimpleDTO toSimple(Series series, User user) {

        SerieSimpleDTO dto = new SerieSimpleDTO();
        if (user == null) {
            dto.setUserLiked(false);
            dto.setInMyList(false);
        } else {
            boolean like = likeRepository.existsByUserAndSeries(user, series);
            boolean myList = myListItemRepository.existsByUserAndContentIdAndType(user, series.getId(), MediaType.SERIES);

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
        dto.setTotalLikes(likeRepository.countBySeries(series));

        return dto;
    }

    public Series toEntity(CreateSerieDTO dto) {

        Series series = new Series();

        series.setTitle(dto.getTitle());
        series.setSynopsis(dto.getSynopsis());
        series.setCategories(dto.getCategories());

        series.setYearRelease(dto.getYearRelease());
        series.setTmdbId(dto.getTmdbId());
        series.setImdbId(dto.getImdbId());
        series.setCountryOrigin(dto.getCountryOrigin());

        series.setMinAge(dto.getMinAge());
        series.setRating(dto.getRating());

        series.setTrailer(dto.getTrailer());

        series.setPosterURL1(dto.getPosterURL1());
        series.setPosterURL2(dto.getPosterURL2());

        series.setBackdropURL1(dto.getBackdropURL1());
        series.setBackdropURL2(dto.getBackdropURL2());
        series.setBackdropURL3(dto.getBackdropURL3());
        series.setBackdropURL4(dto.getBackdropURL4());

        series.setLogoURL1(dto.getLogoURL1());
        series.setLogoURL2(dto.getLogoURL2());

        return series;
    }

}