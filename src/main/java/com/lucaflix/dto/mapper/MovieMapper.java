package com.lucaflix.dto.mapper;


import com.lucaflix.dto.response.movie.MovieCompleteDTO;
import com.lucaflix.dto.response.movie.MovieSimpleDTO;
import com.lucaflix.model.Movie;
import com.lucaflix.model.User;
import com.lucaflix.repository.LikeRepository;
import com.lucaflix.repository.MyListRepository;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class MovieMapper {

    private final MyListRepository myListRepository;
    private final LikeRepository likeRepository;

    public MovieCompleteDTO toComplete(Movie movie, User user) {

        MovieCompleteDTO dto = new MovieCompleteDTO();

        if (user == null) {
            dto.setUserLiked(false);
            dto.setInUserList(false);
        } else {
            boolean like = likeRepository.existsByUserAndMovie(user, movie);
            boolean myList = myListRepository.existsByUserAndMovie(user, movie);

            dto.setUserLiked(like);
            dto.setInUserList(myList);
        }

        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setYearRelease(movie.getYearRelease());
        dto.setMinutesDuration(movie.getMinutesDuration());
        dto.setTmdbId(movie.getTmdbId());
        dto.setImdbId(movie.getImdbId());
        dto.setCountryOrigin(movie.getCountryOrigin());
        dto.setSynopsis(movie.getSynopsis());
        dto.setDateRegistered(movie.getDateRegistered());
        dto.setCategories(movie.getCategories());
        dto.setMinAge(movie.getMinAge());
        dto.setRating(movie.getRating());

        dto.setEmbed1(movie.getEmbed1());
        dto.setEmbed2(movie.getEmbed2());
        dto.setTrailer(movie.getTrailer());

        dto.setPosterURL1(movie.getPosterURL1());
        dto.setPosterURL2(movie.getPosterURL2());

        dto.setBackdropURL1(movie.getBackdropURL1());
        dto.setBackdropURL2(movie.getBackdropURL2());
        dto.setBackdropURL3(movie.getBackdropURL3());
        dto.setBackdropURL4(movie.getBackdropURL4());

        dto.setLogoURL1(movie.getLogoURL1());
        dto.setLogoURL2(movie.getLogoURL2());

        dto.setTotalLikes(likeRepository.countByMovie(movie));

        return dto;
    };

    public MovieSimpleDTO toSimple(Movie movie, User user) {

        MovieSimpleDTO dto = new MovieSimpleDTO();

        if (user == null) {
            dto.setUserLiked(false);
            dto.setInUserList(false);
        } else {
            boolean like = likeRepository.existsByUserAndMovie(user, movie);
            boolean myList = myListRepository.existsByUserAndMovie(user, movie);

            dto.setUserLiked(like);
            dto.setInUserList(myList);
        }

        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setYearRelease(movie.getYearRelease());
        dto.setMinutesDuration(movie.getMinutesDuration());
        dto.setTmdbId(movie.getTmdbId());
        dto.setImdbId(movie.getImdbId());
        dto.setCountryOrigin(movie.getCountryOrigin());
        dto.setCategories(movie.getCategories());
        dto.setMinAge(movie.getMinAge());
        dto.setRating(movie.getRating());

        dto.setPosterURL1(movie.getPosterURL1());
        dto.setPosterURL2(movie.getPosterURL2());

        dto.setTotalLikes(likeRepository.countByMovie(movie));

        return dto;
    };

}