package com.lucaflix.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "episodios")
@Data
public class Episode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id", nullable = false)
    private Series series;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "temp_id", nullable = false)
    private Season season;

    @Column(name = "number_episode", nullable = false)
    private Integer numberEpisode;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @Column(name = "minutes_duration")
    private Integer minutesDuration = 0;

    @Column(name = "date_registered")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDate dateRegistered = LocalDate.now();

    @Column(name = "embed_url_1")
    private String embed1;

    @Column(name = "embed_url_2")
    private String embed2;
}