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

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean isFilme = true;

    @Column(name = "ano_lancamento")
    @Temporal(TemporalType.DATE)
    private Date anoLancamento;

    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos = 0;

    @Column(columnDefinition = "TEXT")
    private String sinopse;

    @Column(name = "data_cadastro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro = new Date();

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

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

    @Column(name = "image_url")
    private String imageURL;

    @OneToMany(mappedBy = "media", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Like> likes;

    @OneToMany(mappedBy = "media", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MinhaLista> minhaLista;

}