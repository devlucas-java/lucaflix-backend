package com.lucaflix.dto.request.others;

import com.lucaflix.model.enums.Categories;
import lombok.Data;

@Data
public class FilterDTO {
    private String title;
    private Double rating;
    private Categories categories;
    private Integer year;
    private String countryOrigin;
}