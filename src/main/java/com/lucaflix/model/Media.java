package com.lucaflix.model;

import com.lucaflix.model.enums.Categoria;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "media")
@Data
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false)
    private boolean isFilme = true;

    @Column(name = "ano_lancamento")
    @Temporal(TemporalType.DATE)
    private Date anoLancamento;

    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos = 0;

    @Column(name = "tmdb_id")
    private String tmdbId = null;

    @Column(name = "imdb_id")
    private String imdbId = null;

    @Column(name = "pais_origem")
    private String paisOrigen = null;

    @Column(columnDefinition = "TEXT")
    private String sinopse;

    @Column(name = "data_cadastro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro = new Date();

    // ALTERAÇÃO PRINCIPAL: ElementCollection para armazenar lista de categorias
    @ElementCollection(targetClass = Categoria.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "media_categoria",
            joinColumns = @JoinColumn(name = "media_id")
    )
    @Column(name = "categoria")
    private List<Categoria> categoria;

    @Column(name = "min_age")
    private String minAge;

    @Column(name = "avaliacao")
    private Double avaliacao;

    @Column(name = "embed_url_1")
    private String embed1;

    @Column(name = "embed_url_2")
    private String embed2;

    @Column(name = "trailer_url")
    private String trailer;

    @Column(name = "image_url_1")
    private String imageURL1;

    @Column(name = "image_url_2")
    private String imageURL2;

    @OneToMany(mappedBy = "media", fetch = FetchType.LAZY)
    private List<Like> likes;

    @OneToMany(mappedBy = "media", fetch = FetchType.LAZY)
    private List<MinhaLista> minhaLista;
}