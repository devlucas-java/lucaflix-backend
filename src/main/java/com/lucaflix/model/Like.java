package com.lucaflix.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "likes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "movie_id"}),
                @UniqueConstraint(columnNames = {"user_id", "serie_id"}),
                @UniqueConstraint(columnNames = {"user_id", "anime_id"})
        })
@Data
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serie_id")
    private Series series;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anime_id")
    private Anime anime;

    @Column(name = "data_like", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateLike = new Date();

    @PrePersist
    @PreUpdate
    private void validateContent() {
        int nonNullCount = 0;
        if (movie != null) nonNullCount++;
        if (series != null) nonNullCount++;
        if (anime != null) nonNullCount++;

        if (nonNullCount != 1) {
            throw new IllegalStateException("Like deve ter exatamente UM tipo de conteúdo: movie, serie ou anime");
        }
    }
}