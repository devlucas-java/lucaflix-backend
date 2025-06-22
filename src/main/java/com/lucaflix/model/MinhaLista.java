package com.lucaflix.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "minha_lista",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "movie_id"}),
                @UniqueConstraint(columnNames = {"user_id", "serie_id"}),
                @UniqueConstraint(columnNames = {"user_id", "anime_id"})
        })
@Data
public class MinhaLista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Para filmes - pode ser null se for série ou anime
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    // Para séries - pode ser null se for movie ou anime
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serie_id")
    private Serie serie;

    // Para animes - pode ser null se for movie ou serie
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anime_id")
    private Anime anime;

    @Column(name = "data_adicao", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAdicao = new Date();

    @Column(name = "data_ultima_visualizacao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataUltimaVisualizacao;

    // Constraint: deve ter APENAS UM tipo de conteúdo (movie OU serie OU anime)
    @PrePersist
    @PreUpdate
    private void validateContent() {
        int nonNullCount = 0;
        if (movie != null) nonNullCount++;
        if (serie != null) nonNullCount++;
        if (anime != null) nonNullCount++;

        if (nonNullCount != 1) {
            throw new IllegalStateException("MinhaLista deve ter exatamente UM tipo de conteúdo: movie, serie ou anime");
        }
    }
}