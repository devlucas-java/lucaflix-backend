package com.lucaflix.dto.mapper;

import com.lucaflix.dto.response.anime.AnimeCompleteDTO;
import com.lucaflix.dto.response.anime.AnimeSimpleDTO;
import com.lucaflix.model.Anime;
import com.lucaflix.model.User;
import com.lucaflix.repository.LikeRepository;
import com.lucaflix.repository.MyListRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnimeMapper {

    private final LikeRepository likeRepository;
    private final MyListRepository myListRepository;

    public AnimeSimpleDTO animeSimpleDTO(Anime anime, User user) {

        AnimeSimpleDTO dto = new AnimeSimpleDTO();

        if (user == null) {
            dto.setUserLiked(false);
            dto.setInUserList(false);
        } else {
            boolean like = likeRepository.existsByUserAndAnime(user, anime);
            boolean myList = myListRepository.existsByUserAndAnime(user, anime);

            dto.setUserLiked(like);
            dto.setInUserList(myList);
        }

        dto.setId(anime.getId());
        dto.setTitle(anime.getTitle());
        dto.setYearRealese(anime.getYearRealese());
        dto.setTmdbId(anime.getTmdbId());
        dto.setImdbId(anime.getImdbId());
        dto.setCountryOrigin(anime.getCountryOrigin());
        dto.setCategories(anime.getCategories());
        dto.setMinAge(anime.getMinAge());
        dto.setRating(anime.getRating());

        dto.setEmbed1(anime.getEmbed1());
        dto.setEmbed2(anime.getEmbed2());
        dto.setTrailer(anime.getTrailer());

        dto.setPosterURL1(anime.getPosterURL1());
        dto.setPosterURL2(anime.getPosterURL2());

        dto.setTotalSeason(anime.getTotalSeason());
        dto.setTotalEpisodes(anime.getTotalEpisodes());

        dto.setTotalLikes((long) anime.getLikes().size());

        return dto;
    }

    public AnimeCompleteDTO animeCompleteDTO(Anime anime, User user) {

        AnimeCompleteDTO dto = new AnimeCompleteDTO();

        if (user == null) {
            dto.setUserLiked(false);
            dto.setInUserList(false);
        } else {
            boolean like = likeRepository.existsByUserAndAnime(user, anime);
            boolean myList = myListRepository.existsByUserAndAnime(user, anime);

            dto.setUserLiked(like);
            dto.setInUserList(myList);
        }

        dto.setId(anime.getId());
        dto.setTitle(anime.getTitle());
        dto.setYearRealese(anime.getYearRealese());
        dto.setTmdbId(anime.getTmdbId());
        dto.setImdbId(anime.getImdbId());
        dto.setCountryOrigin(anime.getCountryOrigin());
        dto.setSynopsis(anime.getSynopsis());
        dto.setDateRegistered(anime.getDateRegistered());
        dto.setCategories(anime.getCategories());
        dto.setMinAge(anime.getMinAge());
        dto.setRating(anime.getRating());

        dto.setEmbed1(anime.getEmbed1());
        dto.setEmbed2(anime.getEmbed2());
        dto.setTrailer(anime.getTrailer());

        dto.setPosterURL1(anime.getPosterURL1());
        dto.setPosterURL2(anime.getPosterURL2());

        dto.setBackdropURL1(anime.getBackdropURL1());
        dto.setBackdropURL2(anime.getBackdropURL2());
        dto.setBackdropURL3(anime.getBackdropURL3());
        dto.setBackdropURL4(anime.getBackdropURL4());

        dto.setLogoURL1(anime.getLogoURL1());
        dto.setLogoURL2(anime.getLogoURL2());

        dto.setTotalSeason(anime.getTotalSeason());
        dto.setTotalEpisodes(anime.getTotalEpisodes());

        dto.setTotalLikes((long) anime.getLikes().size());

        return dto;
    }
}