package com.example.avaliacao1;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Sessão ativa. Tabela de uma única linha (id fixo = 1):
 * existe linha = há usuário logado; não existe = deslogado.
 * Relação 1:1 com Usuario (usuarioId único).
 */
@Entity(
        tableName = "sessao",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "id",
                childColumns = "usuarioId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "usuarioId", unique = true)})
public class Sessao {

    public static final int ID_UNICO = 1;

    @PrimaryKey
    public int id = ID_UNICO;

    public int usuarioId;

    public Sessao() { }

    @Ignore
    public Sessao(int usuarioId) {
        this.usuarioId = usuarioId;
    }
}
