package com.lucaflix.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "season")
@Data
public class Season {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id", nullable = false)
    private Series series;

    @Column(name = "number_season", nullable = false)
    private Integer numberSeason;

    @Column(name = "year_release")
    private Integer yearRelease;

    @Column(name = "date_registered")
    private LocalDateTime dateRegistered = LocalDateTime.now();

    @Column(name = "total_episodes")
    private Integer totalEpisodes = 0;

    @OneToMany(mappedBy = "season", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Episode> episodes = new HashSet<>();
}