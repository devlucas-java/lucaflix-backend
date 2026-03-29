package com.lucaflix.dto.mapper;


import com.lucaflix.dto.request.movie.CreateMovieDTO;
import com.lucaflix.dto.response.movie.MovieCompleteDTO;
import com.lucaflix.dto.response.movie.MovieSimpleDTO;
import com.lucaflix.model.Movie;
import com.lucaflix.model.User;
import com.lucaflix.model.enums.MediaType;
import com.lucaflix.repository.LikeRepository;
import com.lucaflix.repository.MyListItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class MovieMapper {

    private final MyListItemRepository myListItemRepository;
    private final LikeRepository likeRepository;

    public MovieCompleteDTO toComplete(Movie movie, User user) {

        MovieCompleteDTO dto = new MovieCompleteDTO();

        if (user == null) {
            dto.setUserLiked(false);
            dto.setInUserList(false);
        } else {
            boolean like = likeRepository.existsByUserAndMovie(user, movie);
            boolean myList = myListItemRepository.existsByUserAndContentIdAndType(user, movie.getId(), MediaType.MOVIE);

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
            boolean myList = myListItemRepository.existsByUserAndContentIdAndType(user, movie.getId(), MediaType.MOVIE);

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

    public Movie toMovie(CreateMovieDTO dto) {

        Movie movie = new Movie();

        movie.setTitle(dto.getTitle());
        movie.setYearRelease(dto.getYearRelease());
        movie.setMinutesDuration(dto.getMinutesDuration());
        movie.setSynopsis(dto.getSynopsis());
        movie.setCategories(dto.getCategories());

        movie.setTmdbId(dto.getTmdbId());
        movie.setImdbId(dto.getImdbId());
        movie.setCountryOrigin(dto.getCountryOrigin());

        movie.setMinAge(dto.getMinAge());
        movie.setRating(dto.getRating());

        movie.setEmbed1(dto.getEmbed1());
        movie.setEmbed2(dto.getEmbed2());
        movie.setTrailer(dto.getTrailer());

        movie.setPosterURL1(dto.getPosterURL1());
        movie.setPosterURL2(dto.getPosterURL2());

        movie.setBackdropURL1(dto.getBackdropURL1());
        movie.setBackdropURL2(dto.getBackdropURL2());
        movie.setBackdropURL3(dto.getBackdropURL3());
        movie.setBackdropURL4(dto.getBackdropURL4());

        movie.setLogoURL1(dto.getLogoURL1());
        movie.setLogoURL2(dto.getLogoURL2());

        return movie;
    };
}