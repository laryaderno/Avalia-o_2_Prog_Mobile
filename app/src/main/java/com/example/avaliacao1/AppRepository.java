package com.example.avaliacao1;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Única porta de acesso aos dados. Consultas LiveData rodam fora da main thread
 * automaticamente (Room); escritas rodam no executor e devolvem o resultado
 * na main thread via Callback.
 */
public class AppRepository {

    private final UsuarioDao usuarioDao;
    private final SessaoDao sessaoDao;
    private final BiomaDao biomaDao;
    private final ItemDao itemDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());

    public AppRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        usuarioDao = db.usuarioDao();
        sessaoDao = db.sessaoDao();
        biomaDao = db.biomaDao();
        itemDao = db.itemDao();
    }

    // ---------- Sessão / usuário ----------

    public LiveData<Usuario> getUsuarioAtivo() {
        return sessaoDao.observarUsuarioAtivo();
    }

    public void cadastrar(String nome, String email, String senha, byte[] foto,
                          Resultado.Callback<Usuario> cb) {
        executor.execute(() -> {
            Resultado<Usuario> r;
            String emailNorm = normalizarEmail(email);
            if (vazio(nome) || vazio(emailNorm) || vazio(senha)) {
                r = Resultado.erro("Preencha nome, e-mail e senha.");
            } else if (usuarioDao.buscarPorEmail(emailNorm) != null) {
                r = Resultado.erro("Este e-mail já está cadastrado.");
            } else {
                Usuario u = new Usuario();
                u.nome = nome.trim();
                u.email = emailNorm;
                u.senhaSalt = PasswordUtils.gerarSalt();
                u.senhaHash = PasswordUtils.hash(senha, u.senhaSalt);
                u.foto = foto;
                u.id = (int) usuarioDao.inserir(u);
                r = Resultado.ok(u);
            }
            entregar(cb, r);
        });
    }

    /** Valida e-mail/senha; se estiver certo, abre a sessão. */
    public void login(String email, String senha, Resultado.Callback<Usuario> cb) {
        executor.execute(() -> {
            Resultado<Usuario> r;
            Usuario u = vazio(email) ? null : usuarioDao.buscarPorEmail(normalizarEmail(email));
            if (u == null || vazio(senha)
                    || !PasswordUtils.verificar(senha, u.senhaSalt, u.senhaHash)) {
                // Mesma mensagem nos dois casos: não revela se o e-mail existe.
                r = Resultado.erro("E-mail ou senha inválidos.");
            } else {
                sessaoDao.iniciar(new Sessao(u.id));
                r = Resultado.ok(u);
            }
            entregar(cb, r);
        });
    }

    public void logout() {
        executor.execute(sessaoDao::encerrar);
    }

    /**
     * Edição de perfil. novaSenha vazia/null = mantém a senha atual;
     * novaFoto null = mantém a foto atual.
     */
    public void atualizarUsuario(int usuarioId, String nome, String email, String novaSenha,
                                 byte[] novaFoto, Resultado.Callback<Usuario> cb) {
        executor.execute(() -> {
            Resultado<Usuario> r;
            String emailNorm = normalizarEmail(email);
            Usuario u = usuarioDao.buscarPorId(usuarioId);
            if (u == null) {
                r = Resultado.erro("Usuário não encontrado.");
            } else if (vazio(nome) || vazio(emailNorm)) {
                r = Resultado.erro("Preencha nome e e-mail.");
            } else if (usuarioDao.contarEmailDeOutro(emailNorm, usuarioId) > 0) {
                r = Resultado.erro("Este e-mail já está em uso.");
            } else {
                u.nome = nome.trim();
                u.email = emailNorm;
                if (!vazio(novaSenha)) {
                    u.senhaSalt = PasswordUtils.gerarSalt();
                    u.senhaHash = PasswordUtils.hash(novaSenha, u.senhaSalt);
                }
                if (novaFoto != null) {
                    u.foto = novaFoto;
                }
                usuarioDao.atualizar(u);
                r = Resultado.ok(u);
            }
            entregar(cb, r);
        });
    }

    // ---------- Conteúdo ----------

    public LiveData<List<Bioma>> getBiomas() {
        return biomaDao.listarTodos();
    }

    public LiveData<Bioma> getBioma(int biomaId) {
        return biomaDao.observarPorId(biomaId);
    }

    public LiveData<BiomaComItens> getBiomaComItens(int biomaId) {
        return biomaDao.observarComItens(biomaId);
    }

    public LiveData<List<Item>> getTodosItens() {
        return itemDao.listarTodos();
    }

    public LiveData<List<Item>> getItensDoBioma(int biomaId) {
        return itemDao.listarPorBioma(biomaId);
    }

    public LiveData<Item> getItem(int itemId) {
        return itemDao.observarPorId(itemId);
    }

    /** Para o seed (Pessoa 3): roda o bloco fora da main thread. */
    public void executarEmBackground(Runnable tarefa) {
        executor.execute(tarefa);
    }

    public BiomaDao biomaDao() { return biomaDao; }
    public ItemDao itemDao() { return itemDao; }

    // ---------- utilitários ----------

    private <T> void entregar(Resultado.Callback<T> cb, Resultado<T> r) {
        if (cb != null) {
            main.post(() -> cb.aoTerminar(r));
        }
    }

    private static boolean vazio(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
