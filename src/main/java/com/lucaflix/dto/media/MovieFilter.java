package com.lucaflix.dto.media;

import com.lucaflix.model.enums.Categoria;
import lombok.Data;

@Data
public class MovieFilter {
    private Boolean isFilme;
    private String title;
    private Double avaliacao;
    private Categoria categoria;
}