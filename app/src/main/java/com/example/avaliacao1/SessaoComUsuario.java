package com.example.avaliacao1;

import androidx.room.Embedded;
import androidx.room.Relation;

/** Relacionamento 1:1 Sessao -> Usuario. */
public class SessaoComUsuario {

    @Embedded
    public Sessao sessao;

    @Relation(parentColumn = "usuarioId", entityColumn = "id")
    public Usuario usuario;
}
