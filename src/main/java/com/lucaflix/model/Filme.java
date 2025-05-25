package com.lucaflix.model;

import com.lucaflix.model.enums.Categoria;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;
import java.util.Timer;

@Entity
@Table(name = "filmes")
@Data
public class Filme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

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

    @Column(name = "embed_url_1")
    private String embed1;

    @Column(name = "embed_url_2")
    private String embed2;

    @Column(name = "trailer_url")
    private String trailer;

    @Column(name = "image_url")
    private String imageURL;

    @OneToMany(mappedBy = "filme", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Like> likes;

    @OneToMany(mappedBy = "filme", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MinhaLista> minhaLista;
}