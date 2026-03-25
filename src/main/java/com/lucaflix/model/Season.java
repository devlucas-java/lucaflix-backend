package com.lucaflix.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "temporadas")
@Data
public class Season {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serie_id", nullable = false)
    private Series series;

    @Column(name = "number_season", nullable = false)
    private Integer numberSeason;

    @Column(name = "year_release")
    @Temporal(TemporalType.DATE)
    private Integer yearRelease;

    @Column(name = "date_registered")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDate dateRegistered = LocalDate.now();

    @Column(name = "total_episodes")
    private Integer totalEpisodes = 0;

    @OneToMany(mappedBy = "season", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Episode> episodes;
}
