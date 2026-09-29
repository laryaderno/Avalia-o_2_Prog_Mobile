package com.example.avaliacao1;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ItemDao {

    @Insert
    long inserir(Item item);

    @Insert
    void inserirTodos(List<Item> itens);

    @Query("SELECT * FROM item ORDER BY biomaId, id")
    LiveData<List<Item>> listarTodos();

    @Query("SELECT * FROM item WHERE biomaId = :biomaId ORDER BY id")
    LiveData<List<Item>> listarPorBioma(int biomaId);

    @Query("SELECT * FROM item WHERE id = :id LIMIT 1")
    LiveData<Item> observarPorId(int id);
}
