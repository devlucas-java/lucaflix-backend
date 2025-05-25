package com.lucaflix.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "episodios")
@Data
public class Episodio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(name = "numero_episodio", nullable = false)
    private Short numeroEpisodio;

    @Column(columnDefinition = "TEXT")
    private String sinopse;

    @Column(name = "embed_url")
    private String embedUrl;

    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos = 0;

    @ManyToOne
    @JoinColumn(name = "temporada_id", nullable = false)
    private Temporada temporada;
}