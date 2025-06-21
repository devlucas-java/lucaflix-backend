package com.lucaflix.service.utils;

import org.springframework.stereotype.Component;
import java.text.Normalizer;
import java.util.Calendar;
import java.util.Date;

@Component
public class UrlFormatter {

    /**
     * Formata título para URL seguindo o padrão: /id/tipo-titulo-formatado-ano
     * Exemplo: /108/serie-o-jogo-da-viuva-1970
     */
    public String formatTitleForUrl(Long id, String title, Integer anoLancamento) {


        // Formata o título
        String formattedTitle = title
                .toLowerCase()
                .trim()
                // Remove acentos e caracteres especiais
                .replaceAll("[àáâãä]", "a")
                .replaceAll("[èéêë]", "e")
                .replaceAll("[ìíîï]", "i")
                .replaceAll("[òóôõö]", "o")
                .replaceAll("[ùúûü]", "u")
                .replaceAll("[ç]", "c")
                .replaceAll("[ñ]", "n")
                // Remove caracteres especiais, mantém apenas letras, números, espaços e hífens
                .replaceAll("[^a-z0-9\\s-]", "")
                // Substitui espaços por hífens
                .replaceAll("\\s+", "-")
                // Remove hífens duplicados
                .replaceAll("-+", "-")
                // Remove hífens do início e fim
                .replaceAll("^-+|-+$", "");

        return String.format("/%d/%s-%s-%d", id, formattedTitle, anoLancamento);
    }

    /**
     * Normaliza texto removendo acentos usando Normalizer (alternativa mais robusta)
     */
    public String removeAccents(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
