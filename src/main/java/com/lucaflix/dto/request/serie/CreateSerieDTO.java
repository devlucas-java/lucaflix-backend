package com.lucaflix.dto.request.serie;

import com.lucaflix.model.enums.Categories;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class CreateSerieDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters")
    private String title;

    @Size(max = 3000, message = "Synopsis must be max 3000 characters")
    private String synopsis;

    @NotEmpty(message = "At least one category is required")
    private List<Categories> categories;

    private Integer yearRelease;

    private String tmdbId;
    private String imdbId;
    private String countryOrigin;

    private String minAge;

    @DecimalMin(value = "0.0", message = "Rating must be >= 0")
    @DecimalMax(value = "10.0", message = "Rating must be <= 10")
    private Double rating;

    private String trailer;

    @NotBlank(message = "PosterURL1 is required")
    private String posterURL1;
    private String posterURL2;

    private String backdropURL1;
    private String backdropURL2;
    private String backdropURL3;
    private String backdropURL4;

    private String logoURL1;
    private String logoURL2;
}