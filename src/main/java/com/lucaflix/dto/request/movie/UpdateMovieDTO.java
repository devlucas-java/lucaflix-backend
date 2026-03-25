package com.lucaflix.dto.request.movie;

import com.lucaflix.model.enums.Categories;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
public class UpdateMovieDTO {

    @Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters")
    private String title;

    private Integer yearRelease;

    @Min(value = 1, message = "Minutes duration must be greater than 0")
    private Integer minutesDuration;

    @Size(max = 3000, message = "Synopsis must be max 3000 characters")
    private String synopsis;

    private List<Categories> categories;

    private String minAge;

    @DecimalMin(value = "0.0", message = "Rating must be >= 0")
    @DecimalMax(value = "10.0", message = "Rating must be <= 10")
    private Double rating;

    private String embed1;
    private String embed2;

    private String trailer;

    private String tmdbId;
    private String imdbId;
    private String countryOrigin;

    private String posterURL1;
    private String posterURL2;

    private String backdropURL1;
    private String backdropURL2;
    private String backdropURL3;
    private String backdropURL4;

    private String logoURL1;
    private String logoURL2;
}