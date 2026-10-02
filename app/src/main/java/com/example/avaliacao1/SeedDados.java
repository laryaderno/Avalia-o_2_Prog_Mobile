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

        long id1 = db.biomaDao().inserir(new Bioma("Amazônia", context.getString(R.string.desc_amazonia), "som_amazonia"));
        db.itemDao().inserir(new Item((int) id1, "Amazônia - Foto 1", "amazonia_img1"));
        db.itemDao().inserir(new Item((int) id1, "Amazônia - Foto 2", "amazonia_img2"));

        long id2 = db.biomaDao().inserir(new Bioma("Caatinga", context.getString(R.string.desc_caatinga), "som_caatinga"));
        db.itemDao().inserir(new Item((int) id2, "Caatinga - Foto 1", "caatinga_img1"));
        db.itemDao().inserir(new Item((int) id2, "Caatinga - Foto 2", "caatinga_img2"));

        long id3 = db.biomaDao().inserir(new Bioma("Cerrado", context.getString(R.string.desc_cerrado), "som_cerrado"));
        db.itemDao().inserir(new Item((int) id3, "Cerrado - Foto 1", "cerrado_img1"));
        db.itemDao().inserir(new Item((int) id3, "Cerrado - Foto 2", "cerrado_img2"));

        long id4 = db.biomaDao().inserir(new Bioma("Mata Atlântica", context.getString(R.string.desc_mata_atlantica), "som_mata_atlantica"));
        db.itemDao().inserir(new Item((int) id4, "Mata Atlântica - Foto 1", "mata_atlantica_img1"));
        db.itemDao().inserir(new Item((int) id4, "Mata Atlântica - Foto 2", "mata_atlantica_img2"));

        long id5 = db.biomaDao().inserir(new Bioma("Pampa", context.getString(R.string.desc_pampa), "som_pampa"));
        db.itemDao().inserir(new Item((int) id5, "Pampa - Foto 1", "pampa_img1"));
        db.itemDao().inserir(new Item((int) id5, "Pampa - Foto 2", "pampa_img2"));

        long id6 = db.biomaDao().inserir(new Bioma("Pantanal", context.getString(R.string.desc_pantanal), "som_pantanal"));
        db.itemDao().inserir(new Item((int) id6, "Pantanal - Foto 1", "pantanal_img1"));
        db.itemDao().inserir(new Item((int) id6, "Pantanal - Foto 2", "pantanal_img2"));
    }
}
