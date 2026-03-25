package com.lucaflix.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "my_list")
@Data
public class MyList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "my_list_movies",
            joinColumns = @JoinColumn(name = "mylist_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    private Set<Movie> movies = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "my_list_series",
            joinColumns = @JoinColumn(name = "mylist_id"),
            inverseJoinColumns = @JoinColumn(name = "series_id")
    )
    private Set<Series> series = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "my_list_animes",
            joinColumns = @JoinColumn(name = "mylist_id"),
            inverseJoinColumns = @JoinColumn(name = "anime_id")
    )
    private Set<Anime> anime = new HashSet<>();

    @Column(name = "date_registered", nullable = false)
    private LocalDate dateRegistered = LocalDate.now();

    @Column(name = "date_of_last_view")
    private LocalDate dateOfLastView;
}