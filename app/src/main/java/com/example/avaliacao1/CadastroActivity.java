package com.example.avaliacao1;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.avaliacao1.databinding.ActivityCadastroBinding;

/**
 * STUB FUNCIONAL da Pessoa 2: cadastro e edição em texto, SEM foto ainda.
 *
 * TODO (Pessoa 2):
 *  - câmera externa (ACTION_IMAGE_CAPTURE + FileProvider já configurado no Manifest);
 *  - reduzir a foto (lado máx. 512 px, JPEG 80%) e guardar em {@link #foto} (byte[]);
 *  - mostrar a foto atual no modo edição;
 *  - validações de UI (e-mail válido, senha mínima etc.).
 *
 * Modo é escolhido pelo Intent: putExtra(EXTRA_MODO, MODO_CADASTRO ou MODO_EDICAO).
 */
public class CadastroActivity extends AppCompatActivity {

    public static final String EXTRA_MODO = "extra_modo";
    public static final String MODO_CADASTRO = "cadastro";
    public static final String MODO_EDICAO = "edicao";

    private ActivityCadastroBinding binding;
    private AppViewModel viewModel;
    private boolean modoEdicao;
    private Usuario usuarioAtual;   // só no modo edição
    private byte[] foto;            // TODO (Pessoa 2): preencher com a foto da câmera

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
        modoEdicao = MODO_EDICAO.equals(getIntent().getStringExtra(EXTRA_MODO));

        if (modoEdicao) {
            binding.txtTituloCadastro.setText(R.string.edicao_titulo);
            binding.edtSenha.setHint(R.string.hint_nova_senha);
            // Preenche uma única vez; depois da edição o LiveData reemite e não sobrescreve.
            viewModel.getUsuarioAtivo().observe(this, u -> {
                if (u != null && usuarioAtual == null) {
                    usuarioAtual = u;
                    binding.edtNome.setText(u.nome);
                    binding.edtEmail.setText(u.email);
                    // TODO (Pessoa 2): exibir u.foto (byte[]) em binding.imgFoto
                }
            });
        }

        binding.btnSalvar.setOnClickListener(v -> salvar());
        // TODO (Pessoa 2): binding.btnFoto -> abrir câmera
    }

    private void salvar() {
        String nome = binding.edtNome.getText().toString();
        String email = binding.edtEmail.getText().toString();
        String senha = binding.edtSenha.getText().toString();

        if (modoEdicao) {
            if (usuarioAtual == null) return;
            viewModel.atualizarPerfil(usuarioAtual.id, nome, email,
                    senha.isEmpty() ? null : senha, foto, r -> concluir(r, R.string.edicao_sucesso));
        } else {
            viewModel.cadastrar(nome, email, senha, foto, r -> concluir(r, R.string.cadastro_sucesso));
        }
    }

    private void concluir(Resultado<Usuario> r, int msgSucesso) {
        if (r.sucesso) {
            Toast.makeText(this, msgSucesso, Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, r.mensagem, Toast.LENGTH_SHORT).show();
        }
    }
}
