package com.lucaflix.model;

import com.lucaflix.model.enums.Categories;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

@Entity
@Table(name = "anime")
@Data
public class Anime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(name = "year_realese")
    private Integer yearRealese;

    @Column(name = "tmdb_id")
    private String tmdbId;

    @Column(name = "imdb_id")
    private String imdbId;

    @Column(name = "country_origin")
    private String countryOrigin;

    @Column(columnDefinition = "TEXT", length = 3000)
    private String synopsis;

    @Column(name = "date_registered")
    private LocalDate dateRegistered = LocalDate.now();

    @ElementCollection(targetClass = Categories.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "anime_category",
            joinColumns = @JoinColumn(name = "anime_id")
    )
    @Column(name = "categories")
    private List<Categories> categories;

    @Column(name = "min_age")
    private String minAge;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "embed_url_1")
    private String embed1;

    @Column(name = "embed_url_2")
    private String embed2;

    @Column(name = "trailer_url")
    private String trailer;

    @Column(name = "poster_url1")
    private String posterURL1;
    @Column(name = "poster_url2")
    private String posterURL2;

    @Column(name = "backdrop_url1")
    private String backdropURL1;
    @Column(name = "backdrop_url2")
    private String backdropURL2;
    @Column(name = "backdrop_url3")
    private String backdropURL3;
    @Column(name = "backdrop_url4")
    private String backdropURL4;

    @Column(name = "logo_url1")
    private String logoURL1;
    @Column(name = "logo_url2")
    private String logoURL2;

    @Column(name = "total_season")
    private Integer totalSeason = 0;

    @Column(name = "total_episodes")
    private Integer totalEpisodes = 0;

    @OneToMany(mappedBy = "anime", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<Like> likes = new HashSet<>();
}