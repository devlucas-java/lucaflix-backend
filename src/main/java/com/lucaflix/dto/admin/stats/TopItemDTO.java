package com.lucaflix.dto.admin.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopItemDTO {
    private Long id;
    private String title;
    private String type; // "MOVIE" ou "SERIE"
    private Integer likes;
    private Double rating;
    private String category;
    private Integer year;
}
