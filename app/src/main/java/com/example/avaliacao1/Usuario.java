package com.example.avaliacao1;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuario", indices = {@Index(value = "email", unique = true)})
public class Usuario {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String nome = "";

    @NonNull
    public String email = "";

    /** SHA-256(salt + senha), em Base64. Nunca guarda a senha em texto puro. */
    @NonNull
    public String senhaHash = "";

    /** Salt aleatório (Base64), único por usuário. */
    @NonNull
    public String senhaSalt = "";

    /** Foto de perfil em bytes (JPEG). Reduzir a imagem antes de salvar! */
    public byte[] foto;

    public int getId() { return id; }
    @NonNull public String getNome() { return nome; }
    @NonNull public String getEmail() { return email; }
    public byte[] getFoto() { return foto; }
}
