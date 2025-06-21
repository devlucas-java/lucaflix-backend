package com.lucaflix.dto.media.anime;

import com.lucaflix.model.enums.Categoria;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class AnimeCompleteDTO {
    private Long id;
    private String title;
    private String type = "ANIME";
    private Integer anoLancamento;
    private String tmdbId;
    private String imdbId;
    private String paisOrigen;
    private String sinopse;
    private Date dataCadastro;
    private List<Categoria> categoria;
    private String minAge;
    private Double avaliacao;
    private String embed1;
    private String embed2;

    private String trailer;

    private String posterURL1;
    private String posterURL2;

    private String backdropURL1;
    private String backdropURL2;
    private String backdropURL3;
    private String backdropURL4;

    private String logoURL1;
    private String logoURL2;

    private Integer totalTemporadas;
    private Integer totalEpisodios;
    private Long totalLikes;
    private Boolean userLiked;
    private Boolean inUserList;
}