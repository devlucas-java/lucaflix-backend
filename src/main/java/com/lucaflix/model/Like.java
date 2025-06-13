package com.lucaflix.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "likes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "movie_id"}),
                @UniqueConstraint(columnNames = {"user_id", "serie_id"})
        })
@Data
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Para filmes - pode ser null se for série
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    // Para séries - pode ser null se for movie
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serie_id")
    private Serie serie;

    @Column(name = "data_like", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataLike = new Date();

    // Constraint: deve ter OU movie OU serie, não ambos nem nenhum
    @PrePersist
    @PreUpdate
    private void validateContent() {
        if ((movie == null && serie == null) || (movie != null && serie != null)) {
            throw new IllegalStateException("Like deve ter OU movie OU serie, não ambos nem nenhum");
        }
    }
}