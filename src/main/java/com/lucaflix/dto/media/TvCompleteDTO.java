package com.lucaflix.dto.media;

import com.lucaflix.model.enums.Categoria;
import lombok.Data;
import java.util.Date;

@Data
public class TvCompleteDTO {
    private Long id;
    private String title;
    private String paisOrigen;
    private Date dataCadastro;
    private Categoria categoria;
    private String minAge;
    private String embed1;
    private String embed2;
    private String imageURL1;
    private String imageURL2;
    private Long likes;
}