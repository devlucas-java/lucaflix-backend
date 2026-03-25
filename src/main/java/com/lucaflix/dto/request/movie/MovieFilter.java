package com.lucaflix.dto.request.movie;

import com.lucaflix.model.enums.Categories;
import lombok.Data;

@Data
public class MovieFilter {
    private String title;
    private Double avaliacao;
    private Categories categories;
}