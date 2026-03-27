package com.lucaflix.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "episode")
@Data
public class Episode {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
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
    private LocalDateTime dateRegistered = LocalDateTime.now();

    @Column(name = "embed_url_1")
    private String embed1;

    @Column(name = "embed_url_2")
    private String embed2;
}