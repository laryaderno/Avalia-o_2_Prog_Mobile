package com.example.avaliacao1;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

/** Relacionamento 1:N Bioma -> Item. */
public class BiomaComItens {

    @Embedded
    public Bioma bioma;

    @Relation(parentColumn = "id", entityColumn = "biomaId")
    public List<Item> itens;
}
