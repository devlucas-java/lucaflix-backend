package com.lucaflix.model;

import com.lucaflix.model.enums.Categories;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "movie")
@Data
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "year_release")
    private Integer yearRelease;

    @Column(name = "minutes_duration")
    private Integer minutesDuration = 0;

    @Column(name = "tmdb_id")
    private String tmdbId;

    @Column(name = "imdb_id")
    private String imdbId;

    @Column(name = "country_origin")
    private String countryOrigin;

    @Column(columnDefinition = "TEXT", length = 3000)
    private String synopsis;

    @Column(name = "date_registered")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateRegistered = LocalDateTime.now();

    @ElementCollection(targetClass = Categories.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "movie_categories",
            joinColumns = @JoinColumn(name = "movie_id")
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

    @OneToMany(mappedBy = "movie", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<Like> likes = new HashSet<>();
}