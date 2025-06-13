package com.lucaflix.model;

import com.lucaflix.model.enums.Categoria;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "series")
@Data
public class Serie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "ano_lancamento")
    @Temporal(TemporalType.DATE)
    private Date anoLancamento;

    @Column(name = "tmdb_id")
    private String tmdbId;

    @Column(name = "imdb_id")
    private String imdbId;

    @Column(name = "pais_origem")
    private String paisOrigem;

    @Column(columnDefinition = "TEXT")
    private String sinopse;

    @Column(name = "data_cadastro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro = new Date();

    @ElementCollection(targetClass = Categoria.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "serie_categoria",
            joinColumns = @JoinColumn(name = "serie_id")
    )
    @Column(name = "categoria")
    private List<Categoria> categoria;

    @Column(name = "min_age")
    private String minAge;

    @Column(name = "avaliacao")
    private Double avaliacao;

    @Column(name = "trailer_url")
    private String trailer;

    @Column(name = "image_url_1")
    private String imageURL1;

    @Column(name = "image_url_2")
    private String imageURL2;

    @Column(name = "status")
    private String status; // "Em exibição", "Finalizada", "Cancelada"

    @Column(name = "total_temporadas")
    private Integer totalTemporadas = 0;

    @Column(name = "total_episodios")
    private Integer totalEpisodios = 0;

    // Relacionamentos
    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Temporada> temporadas;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Like> likes;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<MinhaLista> minhaLista;
}
