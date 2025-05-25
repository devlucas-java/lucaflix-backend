package com.lucaflix.model.enums;

public enum Categoria {
    ACAO("Ação"),
    COMEDIA("Comédia"),
    FICCAO("Ficção"),
    FICCAO_CIENTIFICA("Ficção Científica"),
    TERROR("Terror"),
    DRAMA("Drama"),
    TEEN("Teen"),
    REALITY("Reality Show"),
    DOCUMENTARIO("Documentário"),
    DESCONHECIDA("Desconhecida");

    private final String descricao;

    Categoria(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}