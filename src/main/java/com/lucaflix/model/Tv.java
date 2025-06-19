package com.lucaflix.model;

import com.lucaflix.model.enums.Categoria;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tv")
@Data
public class Tv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "pais_origem")
    private String paisOrigen = "Brasil";

    @Column(name = "data_cadastro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro = new Date();

    @Column(name = "categoria")
    private Categoria categoria = Categoria.DESCONHECIDA;

    @Column(name = "min_age")
    private String minAge = "10";

    @Column(name = "embed_url_1")
    private String embed1;

    @Column(name = "embed_url_2")
    private String embed2;

    @Column(name = "image_url_1")
    private String imageURL1;

    @Column(name = "image_url_2")
    private String imageURL2;

    private long likes = 0;
}