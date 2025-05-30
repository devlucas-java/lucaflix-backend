package com.lucaflix.dto.media;

import com.lucaflix.model.enums.Categoria;
import lombok.Data;

import java.util.Date;

@Data
public class MediaFilter {
    private Boolean isFilme;
    private String title;
    private Double avaliacao;
    private Date anoLancamentoInicio;
    private Date anoLancamentoFim;
    private Categoria categoria;
}