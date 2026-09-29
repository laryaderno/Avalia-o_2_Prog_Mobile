package com.example.avaliacao1;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

/**
 * ViewModel único do app (escopo da Activity):
 * use new ViewModelProvider(requireActivity()).get(AppViewModel.class).
 */
public class AppViewModel extends AndroidViewModel {

    /** 0 = "Todos" (nenhum bioma filtrado). */
    public static final int TODOS = 0;

    private final AppRepository repository;
    private final LiveData<Usuario> usuarioAtivo;
    private final LiveData<List<Bioma>> biomas;
    private final MutableLiveData<Integer> biomaSelecionadoId = new MutableLiveData<>(TODOS);
    private final LiveData<Bioma> biomaSelecionado;
    private final LiveData<List<Item>> itensFiltrados;

    public AppViewModel(@NonNull Application application) {
        super(application);
        repository = new AppRepository(application);
        usuarioAtivo = repository.getUsuarioAtivo();
        biomas = repository.getBiomas();

        biomaSelecionado = Transformations.switchMap(biomaSelecionadoId, id ->
                id == TODOS ? new MutableLiveData<Bioma>(null) : repository.getBioma(id));

        itensFiltrados = Transformations.switchMap(biomaSelecionadoId, id ->
                id == TODOS ? repository.getTodosItens() : repository.getItensDoBioma(id));
    }

    // ---------- Sessão ----------

    /** null = ninguém logado. */
    public LiveData<Usuario> getUsuarioAtivo() { return usuarioAtivo; }

    public void cadastrar(String nome, String email, String senha, byte[] foto,
                          Resultado.Callback<Usuario> cb) {
        repository.cadastrar(nome, email, senha, foto, cb);
    }

    public void login(String email, String senha, Resultado.Callback<Usuario> cb) {
        repository.login(email, senha, cb);
    }

    public void logout() {
        biomaSelecionadoId.setValue(TODOS);   // próximo usuário começa sem filtro
        repository.logout();
    }

    public void atualizarPerfil(int usuarioId, String nome, String email, String novaSenha,
                                byte[] novaFoto, Resultado.Callback<Usuario> cb) {
        repository.atualizarUsuario(usuarioId, nome, email, novaSenha, novaFoto, cb);
    }

    // ---------- Conteúdo ----------

    public LiveData<List<Bioma>> getBiomas() { return biomas; }

    /** Chamado pelo Spinner. Passe AppViewModel.TODOS para limpar o filtro. */
    public void selecionarBioma(int biomaId) { biomaSelecionadoId.setValue(biomaId); }

    public LiveData<Integer> getBiomaSelecionadoId() { return biomaSelecionadoId; }

    /** Bioma escolhido no Spinner (null quando "Todos"). */
    public LiveData<Bioma> getBiomaSelecionado() { return biomaSelecionado; }

    /** Itens do bioma selecionado, ou todos se nenhum filtro. */
    public LiveData<List<Item>> getItensFiltrados() { return itensFiltrados; }

    public LiveData<Bioma> getBioma(int biomaId) { return repository.getBioma(biomaId); }

    public LiveData<Item> getItem(int itemId) { return repository.getItem(itemId); }

    public AppRepository getRepository() { return repository; }
}