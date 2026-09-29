package com.example.avaliacao1;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "bioma")
public class Bioma {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String nome = "";

    @NonNull
    public String descricao = "";

    /** Caminho/nome do áudio (String). Ex.: "som_amazonia" (arquivo em res/raw). */
    public String caminhoAudio;

    public Bioma() { }

    @Ignore
    public Bioma(@NonNull String nome, @NonNull String descricao, String caminhoAudio) {
        this.nome = nome;
        this.descricao = descricao;
        this.caminhoAudio = caminhoAudio;
    }

    public int getId() { return id; }
    @NonNull public String getNome() { return nome; }
    @NonNull public String getDescricao() { return descricao; }
    public String getCaminhoAudio() { return caminhoAudio; }
}
