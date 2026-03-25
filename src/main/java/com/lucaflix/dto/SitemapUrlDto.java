package com.lucaflix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SitemapUrlDto {

    /**
     * URL completa da página
     */
    private String loc;

    /**
     * Data da última modificação (formato: YYYY-MM-DD)
     */
    private String lastmod;

    /**
     * Frequência de mudança da página
     * Valores: always, hourly, daily, weekly, monthly, yearly, never
     */
    private String changefreq;

    /**
     * Prioridade da URL (0.0 a 1.0)
     */
    private String priority;

    /**
     * Construtor para URLs básicas (sem changefreq e priority)
     */
    public SitemapUrlDto(String loc, String lastmod) {
        this.loc = loc;
        this.lastmod = lastmod;
    }
}