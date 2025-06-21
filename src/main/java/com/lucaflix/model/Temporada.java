package com.lucaflix.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "temporadas")
@Data
public class Temporada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serie_id", nullable = false)
    private Serie serie;

    @Column(name = "numero_temporada", nullable = false)
    private Integer numeroTemporada;

    @Column(name = "ano_lancamento")
    @Temporal(TemporalType.DATE)
    private Integer anoLancamento;

    @Column(name = "data_cadastro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro = new Date();

    @Column(name = "total_episodios")
    private Integer totalEpisodios = 0;

    @OneToMany(mappedBy = "temporada", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Episodio> episodios;
}
