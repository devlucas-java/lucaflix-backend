package com.lucaflix.dto.request.serie;

import com.lucaflix.model.enums.Categories;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UpdateSerieDTO {

    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    private Integer yearRelease;

    private String tmdbId;
    private String imdbId;
    private String countryOrigin;

    @Size(max = 3000, message = "Synopsis must be at most 3000 characters")
    private String synopsis;

    private List<Categories> categories;

    private String minAge;

    @DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
    @DecimalMax(value = "10.0", message = "Rating must be at most 10.0")
    private Double rating;

    private String trailer;

    private String posterURL1;
    private String posterURL2;

    private String backdropURL1;
    private String backdropURL2;
    private String backdropURL3;
    private String backdropURL4;

    private String logoURL1;
    private String logoURL2;
}