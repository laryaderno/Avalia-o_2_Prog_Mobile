package com.example.avaliacao1;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long inserir(Usuario usuario);

    @Update
    void atualizar(Usuario usuario);

    @Query("SELECT * FROM usuario WHERE email = :email LIMIT 1")
    Usuario buscarPorEmail(String email);

    @Query("SELECT * FROM usuario WHERE id = :id LIMIT 1")
    Usuario buscarPorId(int id);

    /** Usado na edição: existe OUTRO usuário com este e-mail? */
    @Query("SELECT COUNT(*) FROM usuario WHERE email = :email AND id != :idIgnorado")
    int contarEmailDeOutro(String email, int idIgnorado);
}
