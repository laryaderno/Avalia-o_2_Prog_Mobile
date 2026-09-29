package com.example.avaliacao1;

public class Bioma {

    private final String nome;
    private final String descricao;
    private final int imagemPrincipal;
    private final int imagemSecundaria;
    private final int som;

    public Bioma(String nome, String descricao, int imagemPrincipal, int imagemSecundaria, int som) {
        this.nome = nome;
        this.descricao = descricao;
        this.imagemPrincipal = imagemPrincipal;
        this.imagemSecundaria = imagemSecundaria;
        this.som = som;
    }


    public Bioma(String nome, String descricao, int imagemPrincipal, int imagemSecundaria) {
        this.nome = nome;
        this.descricao = descricao;
        this.imagemPrincipal = imagemPrincipal;
        this.imagemSecundaria = imagemSecundaria;
        this.som = 0;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getImagemPrincipal() {
        return imagemPrincipal;
    }

    public int getImagemSecundaria() {
        return imagemSecundaria;
    }

    public int getSom() {
        return som;
    }
}