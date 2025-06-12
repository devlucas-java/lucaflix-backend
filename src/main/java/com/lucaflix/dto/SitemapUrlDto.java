package com.lucaflix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SitemapUrlDto {
    private String loc;           // URL completa
    private String lastmod;       // Data última modificação
    private String changefreq;    // Frequência de mudança
    private String priority;      // Prioridade (0.0 a 1.0)
}
