package com.example.avaliacao1;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** Elemento de um bioma (relação 1:N com Bioma). */
@Entity(
        tableName = "item",
        foreignKeys = @ForeignKey(
                entity = Bioma.class,
                parentColumns = "id",
                childColumns = "biomaId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("biomaId")})
public class Item {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int biomaId;

    @NonNull
    public String nome = "";

    /** Caminho/nome da imagem (String). Ex.: "amazonia_img1" (drawable). */
    public String caminhoImagem;

    public Item() { }

    @Ignore
    public Item(int biomaId, @NonNull String nome, String caminhoImagem) {
        this.biomaId = biomaId;
        this.nome = nome;
        this.caminhoImagem = caminhoImagem;
    }

    public int getId() { return id; }
    public int getBiomaId() { return biomaId; }
    @NonNull public String getNome() { return nome; }
    public String getCaminhoImagem() { return caminhoImagem; }
}
