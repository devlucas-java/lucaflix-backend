package com.lucaflix.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "likes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "filme_id"}),
                @UniqueConstraint(columnNames = {"user_id", "serie_id"})
        })
@Data
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "filme_id")
    private Filme filme;

    @ManyToOne
    @JoinColumn(name = "serie_id")
    private Serie serie;

    @Column(name = "data_like")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataLike = new Date();

    // Constraint: um like deve estar associado a UM filme OU UMA série, não ambos
    @PrePersist
    @PreUpdate
    private void validateLike() {
        if ((filme == null && serie == null) || (filme != null && serie != null)) {
            throw new IllegalStateException("Like deve estar associado a exatamente um filme OU uma série");
        }
    }
}
