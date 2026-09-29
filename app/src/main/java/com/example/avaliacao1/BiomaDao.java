package com.example.avaliacao1;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface BiomaDao {

    @Insert
    long inserir(Bioma bioma);

    @Insert
    List<Long> inserirTodos(List<Bioma> biomas);

    @Query("SELECT COUNT(*) FROM bioma")
    int contar();

    @Query("SELECT * FROM bioma ORDER BY id")
    LiveData<List<Bioma>> listarTodos();

    @Query("SELECT * FROM bioma WHERE id = :id LIMIT 1")
    LiveData<Bioma> observarPorId(int id);

    @Transaction
    @Query("SELECT * FROM bioma WHERE id = :id LIMIT 1")
    LiveData<BiomaComItens> observarComItens(int id);
}
