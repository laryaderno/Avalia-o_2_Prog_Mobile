package com.example.avaliacao1;

public class Atualizacao {

    private int imagem;
    private String titulo;
    private String descricao;

    public Atualizacao(int imagem, String titulo, String descricao) {
        this.imagem = imagem;
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public int getImagem() {
        return imagem;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }
}