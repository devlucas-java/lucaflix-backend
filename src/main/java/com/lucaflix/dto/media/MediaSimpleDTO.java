package com.lucaflix.dto.media;

import com.lucaflix.model.enums.Categoria;
import lombok.Data;
import java.util.Date;

@Data
public class MediaSimpleDTO {
    private Long id;
    private String title;
    private boolean isFilme;
    private Date anoLancamento;
    private Integer duracaoMinutos;
    private Categoria categoria;
    private String minAge;
    private Double avaliacao;
    private String imageURL;
    private Long totalLikes;
}