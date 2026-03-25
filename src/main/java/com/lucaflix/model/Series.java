package com.lucaflix.model;

import com.lucaflix.model.enums.Categories;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "series")
@Data
public class Series {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "year_release")
    private Integer yearRelease;

    @Column(name = "tmdb_id")
    private String tmdbId;

    @Column(name = "imdb_id")
    private String imdbId;

    @Column(name = "country_origin")
    private String countryOrigin;

    @Column(columnDefinition = "TEXT", length = 3000)
    private String synopsis;

    @Column(name = "date_registered")
    private LocalDateTime dateRegistered = LocalDateTime.now();

    @ElementCollection(targetClass = Categories.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "series_category",
            joinColumns = @JoinColumn(name = "series_id")
    )
    @Column(name = "category")
    private List<Categories> categories;

    @Column(name = "min_age")
    private String minAge;

    @Column(name = "rating")
    private Double rating;

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

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Season> seasons = new HashSet<>();

    @OneToMany(mappedBy = "series", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<Like> likes = new HashSet<>();

    @ManyToMany(mappedBy = "series", fetch = FetchType.LAZY)
    private Set<MyList> myLists = new HashSet<>();
}