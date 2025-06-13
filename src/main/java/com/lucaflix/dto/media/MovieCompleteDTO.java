package com.lucaflix.dto.media;

import com.lucaflix.model.enums.Categoria;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class MovieCompleteDTO {
    private Long id;
    private String title;
    private boolean isFilme;
    private Date anoLancamento;
    private Integer duracaoMinutos;
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
    private String imageURL1;
    private String imageURL2;
    private Long totalLikes;
    private boolean userLiked;
    private boolean inUserList;
}