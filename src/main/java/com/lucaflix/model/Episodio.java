package com.lucaflix.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "episodios")
@Data
public class Episodio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serie_id", nullable = false)
    private Serie serie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "temporada_id", nullable = false)
    private Temporada temporada;

    @Column(name = "numero_episodio", nullable = false)
    private Integer numeroEpisodio;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String sinopse;

    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos = 0;

    @Column(name = "data_cadastro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro = new Date();

    @Column(name = "embed_url_1")
    private String embed1;

    @Column(name = "embed_url_2")
    private String embed2;
}