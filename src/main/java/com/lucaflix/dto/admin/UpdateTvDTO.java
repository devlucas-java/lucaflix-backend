package com.lucaflix.dto.admin;

import com.lucaflix.model.enums.Categories;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateTvDTO {

    @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
    private String title;

    @Size(max = 100, message = "País de origem deve ter no máximo 100 caracteres")
    private String paisOrigen;

    private Categories categories;

    private String minAge;

    private String embed1;
    private String embed2;
    private String imageURL1;
    private String imageURL2;
}