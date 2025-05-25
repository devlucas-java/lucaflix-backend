package com.lucaflix.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "minha_lista",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "filme_id"}),
                @UniqueConstraint(columnNames = {"user_id", "serie_id"})
        })
@Data
public class MinhaLista {

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

    @Column(name = "data_adicao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAdicao = new Date();

    // Para filmes: true = assistido, false = não assistido
    // Para séries: sempre false (controle é por episódio)
    @Column(name = "assistido")
    private Boolean assistido = false;

    // Apenas para séries: quantos episódios já assistiu
    @Column(name = "episodios_assistidos")
    private Integer episodiosAssistidos = 0;

    @Column(name = "data_ultima_visualizacao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataUltimaVisualizacao;

    // Constraint: um item da lista deve estar associado a UM filme OU UMA série, não ambos
    @PrePersist
    @PreUpdate
    private void validateMinhaLista() {
        if ((filme == null && serie == null) || (filme != null && serie != null)) {
            throw new IllegalStateException("Item da lista deve estar associado a exatamente um filme OU uma série");
        }
    }
}