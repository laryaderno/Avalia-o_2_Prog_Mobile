package com.example.avaliacao1;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface SessaoDao {

    /** Sobrescreve a sessão anterior (id fixo = 1). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void iniciar(Sessao sessao);

    @Query("DELETE FROM sessao")
    void encerrar();

    @Transaction
    @Query("SELECT * FROM sessao LIMIT 1")
    LiveData<SessaoComUsuario> observarSessao();

    /** Emite o usuário logado, ou null se não houver sessão. */
    @Query("SELECT u.* FROM usuario u INNER JOIN sessao s ON s.usuarioId = u.id LIMIT 1")
    LiveData<Usuario> observarUsuarioAtivo();
}
