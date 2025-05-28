package com.lucaflix.dto.media;

import com.lucaflix.model.enums.Categoria;
import lombok.Data;
import java.util.Date;

@Data
public class MediaCompleteDTO {
    private Long id;
    private String title;
    private boolean isFilme;
    private Date anoLancamento;
    private Integer duracaoMinutos;
    private String sinopse;
    private Date dataCadastro;
    private Categoria categoria;
    private String minAge;
    private Double avaliacao;
    private String embed1;
    private String embed2;
    private String trailer;
    private String imageURL;
    private Long totalLikes;
    private boolean userLiked;
    private boolean inUserList;
}