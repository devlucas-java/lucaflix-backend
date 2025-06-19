package com.lucaflix.dto.media.movie;

import com.lucaflix.model.enums.Categoria;
import lombok.Data;

@Data
public class MovieFilter {
    private String title;
    private Double avaliacao;
    private Categoria categoria;
}