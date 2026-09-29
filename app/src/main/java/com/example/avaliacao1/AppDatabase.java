package com.example.avaliacao1;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.annotation.NonNull;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

@Database(
        entities = {Usuario.class, Sessao.class, Bioma.class, Item.class},
        version = 1,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String NOME_BANCO = "orbis.db";
    private static volatile AppDatabase instancia;

    public abstract UsuarioDao usuarioDao();
    public abstract SessaoDao sessaoDao();
    public abstract BiomaDao biomaDao();
    public abstract ItemDao itemDao();

    public static AppDatabase getInstance(Context context) {
        if (instancia == null) {
            synchronized (AppDatabase.class) {
                if (instancia == null) {
                    final Context app = context.getApplicationContext();
                    instancia = Room.databaseBuilder(app, AppDatabase.class, NOME_BANCO)
                            // Popula biomas/itens (SeedDados = Pessoa 3) sempre que o banco
                            // estiver vazio. Idempotente: se o app fechar no meio do seed,
                            // ele é refeito na próxima abertura.
                            .addCallback(new Callback() {
                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase sqlite) {
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        AppDatabase db = getInstance(app);
                                        if (db.biomaDao().contar() == 0) {
                                            SeedDados.popular(app, db);
                                        }
                                    });
                                }
                            })
                            // Sem migrações na avaliação: se mudar o schema, recria o banco.
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instancia;
    }
}