package com.lucaflix.dto.request.others;

import com.lucaflix.model.enums.Categories;
import com.lucaflix.model.enums.MediaType;
import lombok.Data;

import java.util.List;

@Data
public class FilterDTO {

    private String title;

    private Double minRating;
    private Double maxRating;

    private List<Categories> categories;

    private Integer yearStart;
    private Integer yearEnd;

    private String countryOrigin;
}