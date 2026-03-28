package com.lucaflix.dto.mapper;

import com.lucaflix.dto.request.anime.CreateAnimeDTO;
import com.lucaflix.dto.response.anime.AnimeCompleteDTO;
import com.lucaflix.dto.response.anime.AnimeSimpleDTO;
import com.lucaflix.model.Anime;
import com.lucaflix.model.User;
import com.lucaflix.model.enums.MediaType;
import com.lucaflix.repository.LikeRepository;
import com.lucaflix.repository.MyListItemRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnimeMapper {

    private final LikeRepository likeRepository;
    private final MyListItemRepository myListItemRepository;

    public AnimeSimpleDTO toSimple(Anime anime, User user) {

        AnimeSimpleDTO dto = new AnimeSimpleDTO();

        if (user == null) {
            dto.setUserLiked(false);
            dto.setInUserList(false);
        } else {
            boolean like = likeRepository.existsByUserAndAnime(user, anime);
            boolean myList = myListItemRepository.existsByUserAndContentIdAndType(user, anime.getId(), MediaType.ANIME);

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

        dto.setTotalLikes(likeRepository.countByAnime(anime));

        return dto;
    }

    public AnimeCompleteDTO toComplete(Anime anime, User user) {

        AnimeCompleteDTO dto = new AnimeCompleteDTO();

        if (user == null) {
            dto.setUserLiked(false);
            dto.setInUserList(false);
        } else {
            boolean like = likeRepository.existsByUserAndAnime(user, anime);
            boolean myList = myListItemRepository.existsByUserAndContentIdAndType(user, anime.getId(), MediaType.ANIME);

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

        dto.setTotalLikes(likeRepository.countByAnime(anime));

        return dto;
    }

    public Anime toEntity(CreateAnimeDTO dto) {

        Anime anime = new Anime();

        anime.setTitle(dto.getTitle().trim());
        anime.setYearRealese(dto.getYearRealese());
        anime.setTmdbId(dto.getTmdbId());
        anime.setImdbId(dto.getImdbId());
        anime.setCountryOrigin(dto.getCountryOrigin());
        anime.setSynopsis(dto.getSynopsis());

        anime.setCategories(
                dto.getCategories()
                        .stream()
                        .distinct()
                        .toList()
        );

        anime.setMinAge(dto.getMinAge());
        anime.setRating(dto.getRating());

        anime.setEmbed1(dto.getEmbed1());
        anime.setEmbed2(dto.getEmbed2());
        anime.setTrailer(dto.getTrailer());

        anime.setPosterURL1(dto.getPosterURL1());
        anime.setPosterURL2(dto.getPosterURL2());

        anime.setBackdropURL1(dto.getBackdropURL1());
        anime.setBackdropURL2(dto.getBackdropURL2());
        anime.setBackdropURL3(dto.getBackdropURL3());
        anime.setBackdropURL4(dto.getBackdropURL4());

        anime.setLogoURL1(dto.getLogoURL1());
        anime.setLogoURL2(dto.getLogoURL2());

        anime.setTotalSeason(dto.getTotalSeason() != null ? dto.getTotalSeason() : 0);
        anime.setTotalEpisodes(dto.getTotalEpisodes() != null ? dto.getTotalEpisodes() : 0);

        return anime;
    }
}