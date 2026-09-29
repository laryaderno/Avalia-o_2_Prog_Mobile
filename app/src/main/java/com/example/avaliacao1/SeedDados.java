package com.example.avaliacao1;

/**
 * TODO (Pessoa 3): popular o banco na primeira execução.
 * É chamado UMA vez (criação do banco), já fora da main thread, pelo AppDatabase.
 * Convenção: Bioma.caminhoAudio = "som_amazonia" (res/raw); Item.caminhoImagem = "amazonia_img1" (drawable).
 *
 * Exemplo:
 *   long id = db.biomaDao().inserir(new Bioma("Amazônia", "descrição...", "som_amazonia"));
 *   db.itemDao().inserir(new Item((int) id, "Amazônia - foto 1", "amazonia_img1"));
 *
 * Dica: textos longos ficam no strings.xml; use context.getString(R.string.desc_amazonia).
 */
public final class SeedDados {

    private SeedDados() { }

    public static void popular(android.content.Context context, AppDatabase db) {
        // TODO (Pessoa 3): 6 biomas + itens (2 imagens por bioma, como na Avaliação 1)
    }
}
