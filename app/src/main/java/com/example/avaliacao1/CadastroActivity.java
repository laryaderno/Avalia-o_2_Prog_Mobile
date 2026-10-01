package com.example.avaliacao1;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.avaliacao1.databinding.ActivityCadastroBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class CadastroActivity extends AppCompatActivity {

    public static final String EXTRA_MODO = "extra_modo";
    public static final String MODO_CADASTRO = "cadastro";
    public static final String MODO_EDICAO = "edicao";

    private static final int SENHA_MINIMA = 6;
    private static final int LADO_MAXIMO = 512;

    private ActivityCadastroBinding binding;
    private AppViewModel viewModel;
    private EstadoCadastro estado;
    private boolean modoEdicao;
    private boolean sessaoPronta;

    private final ActivityResultLauncher<Uri> camera =
            registerForActivityResult(
                    new ActivityResultContracts.TakePicture(),
                    sucesso -> {
                        estado.cameraAberta = false;

                        if (sucesso && estado.caminhoCamera != null) {
                            processarFoto();
                        } else {
                            apagarTemporario();
                            atualizarControles();
                        }
                    }
            );

    private final ActivityResultLauncher<String> permissaoCamera =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    concedida -> {
                        estado.solicitandoPermissao = false;

                        if (concedida) {
                            abrirCamera();
                        } else {
                            Toast.makeText(
                                    this,
                                    R.string.p2_permissao_negada,
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                        atualizarControles();
                    }
            );

    public static class EstadoCadastro extends ViewModel {
        boolean restaurado;
        boolean preenchido;
        boolean enviando;
        boolean processandoFoto;
        boolean cameraAberta;
        boolean solicitandoPermissao;
        boolean fotoAlterada;

        int usuarioId;
        byte[] foto;
        String caminhoCamera;

        final MutableLiveData<Resultado<Usuario>> resultado =
                new MutableLiveData<>();

        final MutableLiveData<Resultado<byte[]>> resultadoFoto =
                new MutableLiveData<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(
                binding.getRoot(),
                (view, insets) -> {
                    Insets barras = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                                    | WindowInsetsCompat.Type.ime()
                    );

                    view.setPadding(
                            barras.left,
                            barras.top,
                            barras.right,
                            barras.bottom
                    );

                    return insets;
                }
        );

        ViewCompat.requestApplyInsets(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AppViewModel.class);
        estado = new ViewModelProvider(this).get(EstadoCadastro.class);

        modoEdicao = MODO_EDICAO.equals(
                getIntent().getStringExtra(EXTRA_MODO)
        );

        restaurarEstado(savedInstanceState);

        if (modoEdicao) {
            binding.txtTituloCadastro.setText(R.string.edicao_titulo);
            binding.edtSenha.setHint(R.string.hint_nova_senha);

            viewModel.getUsuarioAtivo().observe(this, usuario -> {
                if (usuario == null
                        || (estado.usuarioId != 0
                        && estado.usuarioId != usuario.id)) {

                    Toast.makeText(
                            this,
                            R.string.p2_sessao_encerrada,
                            Toast.LENGTH_LONG
                    ).show();

                    finish();
                    return;
                }

                sessaoPronta = true;
                estado.usuarioId = usuario.id;

                if (!estado.preenchido) {
                    binding.edtNome.setText(usuario.nome);
                    binding.edtEmail.setText(usuario.email);

                    if (!estado.fotoAlterada) {
                        estado.foto = usuario.foto;
                    }

                    estado.preenchido = true;
                    mostrarFoto();
                }

                atualizarControles();
            });
        } else {
            sessaoPronta = true;
        }

        estado.resultado.observe(this, resultado -> {
            if (resultado == null) return;

            estado.resultado.setValue(null);
            atualizarControles();

            if (resultado.sucesso) {
                Toast.makeText(
                        this,
                        modoEdicao
                                ? R.string.edicao_sucesso
                                : R.string.cadastro_sucesso,
                        Toast.LENGTH_LONG
                ).show();

                finish();
            } else {
                Toast.makeText(
                        this,
                        resultado.mensagem,
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        estado.resultadoFoto.observe(this, resultado -> {
            if (resultado == null) return;

            estado.resultadoFoto.setValue(null);
            estado.processandoFoto = false;

            apagarTemporario();

            if (resultado.sucesso) {
                estado.foto = resultado.dado;
                estado.fotoAlterada = true;
                mostrarFoto();
            } else {
                Toast.makeText(
                        this,
                        R.string.p2_foto_erro,
                        Toast.LENGTH_LONG
                ).show();
            }

            atualizarControles();
        });

        binding.btnSalvar.setOnClickListener(v -> salvar());
        binding.btnFoto.setOnClickListener(v -> checarPermissao());

        binding.edtSenha.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                salvar();
                return true;
            }
            return false;
        });

        mostrarFoto();
        atualizarControles();

        if (estado.caminhoCamera != null
                && !estado.cameraAberta
                && !estado.processandoFoto) {
            processarFoto();
        }
    }

    private void salvar() {
        if (ocupado() || !sessaoPronta) return;

        String nome = binding.edtNome.getText().toString().trim();
        String email = binding.edtEmail.getText().toString().trim();
        String senha = binding.edtSenha.getText().toString();

        binding.edtNome.setError(null);
        binding.edtEmail.setError(null);
        binding.edtSenha.setError(null);

        if (nome.isEmpty()) {
            binding.edtNome.setError(
                    getString(R.string.p2_nome_obrigatorio)
            );
            binding.edtNome.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.setError(
                    getString(R.string.p2_email_invalido)
            );
            binding.edtEmail.requestFocus();
            return;
        }

        if ((!modoEdicao || !senha.isEmpty())
                && (senha.trim().isEmpty()
                || senha.length() < SENHA_MINIMA)) {

            binding.edtSenha.setError(
                    getString(R.string.p2_senha_minima, SENHA_MINIMA)
            );
            binding.edtSenha.requestFocus();
            return;
        }

        if (estado.foto == null || estado.foto.length == 0) {
            Toast.makeText(
                    this,
                    R.string.p2_foto_obrigatoria,
                    Toast.LENGTH_LONG
            ).show();

            binding.btnFoto.requestFocus();
            return;
        }

        estado.enviando = true;
        atualizarControles();

        EstadoCadastro operacao = estado;

        Resultado.Callback<Usuario> callback = resultado -> {
            operacao.enviando = false;
            operacao.resultado.setValue(resultado);
        };

        if (modoEdicao) {
            viewModel.atualizarPerfil(
                    estado.usuarioId,
                    nome,
                    email,
                    senha.isEmpty() ? null : senha,
                    estado.fotoAlterada ? estado.foto : null,
                    callback
            );
        } else {
            viewModel.cadastrar(
                    nome,
                    email,
                    senha,
                    estado.foto,
                    callback
            );
        }
    }

    private void checarPermissao() {
        if (ocupado() || !sessaoPronta) return;

        if (!permissaoCameraDeclarada()
                || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {
            abrirCamera();
        } else {
            estado.solicitandoPermissao = true;
            atualizarControles();
            permissaoCamera.launch(Manifest.permission.CAMERA);
        }
    }

    private boolean permissaoCameraDeclarada() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_PERMISSIONS
            );

            if (info.requestedPermissions != null) {
                for (String permissao : info.requestedPermissions) {
                    if (Manifest.permission.CAMERA.equals(permissao)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException erro) {
            return false;
        }

        return false;
    }

    private void abrirCamera() {
        if (ocupado()) return;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(
                    this,
                    R.string.p2_camera_indisponivel,
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        try {
            File pasta = new File(getCacheDir(), "fotos");

            if (!pasta.exists() && !pasta.mkdirs()) {
                throw new IOException();
            }

            File arquivo = File.createTempFile(
                    "perfil_",
                    ".jpg",
                    pasta
            );

            estado.caminhoCamera = arquivo.getAbsolutePath();

            Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    arquivo
            );

            estado.cameraAberta = true;
            atualizarControles();
            camera.launch(uri);

        } catch (ActivityNotFoundException
                 | SecurityException
                 | IllegalArgumentException
                 | IOException erro) {

            estado.cameraAberta = false;
            apagarTemporario();
            atualizarControles();

            Toast.makeText(
                    this,
                    R.string.p2_camera_erro,
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void processarFoto() {
        if (estado.processandoFoto || estado.caminhoCamera == null) {
            return;
        }

        estado.processandoFoto = true;
        atualizarControles();

        EstadoCadastro operacao = estado;
        String caminho = estado.caminhoCamera;

        viewModel.getRepository().executarEmBackground(() -> {
            try {
                operacao.resultadoFoto.postValue(
                        Resultado.ok(converterFoto(caminho))
                );
            } catch (IOException | RuntimeException erro) {
                operacao.resultadoFoto.postValue(
                        Resultado.erro("foto")
                );
            }
        });
    }

    private static byte[] converterFoto(String caminho) throws IOException {
        BitmapFactory.Options opcoes = new BitmapFactory.Options();
        opcoes.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(caminho, opcoes);

        if (opcoes.outWidth <= 0 || opcoes.outHeight <= 0) {
            throw new IOException();
        }

        opcoes.inSampleSize = 1;

        while (Math.max(opcoes.outWidth, opcoes.outHeight)
                / opcoes.inSampleSize > LADO_MAXIMO * 2) {
            opcoes.inSampleSize *= 2;
        }

        opcoes.inJustDecodeBounds = false;

        Bitmap imagem = BitmapFactory.decodeFile(caminho, opcoes);

        if (imagem == null) {
            throw new IOException();
        }

        try {
            float proporcao = Math.min(
                    1f,
                    (float) LADO_MAXIMO
                            / Math.max(imagem.getWidth(), imagem.getHeight())
            );

            Bitmap reduzida = Bitmap.createScaledBitmap(
                    imagem,
                    Math.max(1, Math.round(imagem.getWidth() * proporcao)),
                    Math.max(1, Math.round(imagem.getHeight() * proporcao)),
                    true
            );

            if (reduzida != imagem) {
                imagem.recycle();
                imagem = reduzida;
            }

            ExifInterface exif = new ExifInterface(caminho);

            int orientacao = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
            );

            Matrix matriz = new Matrix();

            switch (orientacao) {
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matriz.setScale(-1, 1);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    matriz.setRotate(180);
                    break;

                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matriz.setScale(1, -1);
                    break;

                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matriz.setRotate(90);
                    matriz.postScale(-1, 1);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_90:
                    matriz.setRotate(90);
                    break;

                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matriz.setRotate(-90);
                    matriz.postScale(-1, 1);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    matriz.setRotate(270);
                    break;

                default:
                    break;
            }

            if (!matriz.isIdentity()) {
                Bitmap orientada = Bitmap.createBitmap(
                        imagem,
                        0,
                        0,
                        imagem.getWidth(),
                        imagem.getHeight(),
                        matriz,
                        true
                );

                if (orientada != imagem) {
                    imagem.recycle();
                    imagem = orientada;
                }
            }

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            if (!imagem.compress(Bitmap.CompressFormat.JPEG, 80, bytes)) {
                throw new IOException();
            }

            return bytes.toByteArray();

        } finally {
            imagem.recycle();
        }
    }

    private void mostrarFoto() {
        Bitmap imagem = estado.foto == null
                ? null
                : BitmapFactory.decodeByteArray(
                estado.foto,
                0,
                estado.foto.length
        );

        if (imagem != null) {
            binding.imgFoto.setImageBitmap(imagem);
            binding.imgFoto.setPadding(0, 0, 0, 0);
        } else {
            binding.imgFoto.setImageResource(
                    android.R.drawable.ic_menu_camera
            );

            int espaco = Math.round(
                    28 * getResources().getDisplayMetrics().density
            );

            binding.imgFoto.setPadding(
                    espaco,
                    espaco,
                    espaco,
                    espaco
            );
        }
    }

    private boolean ocupado() {
        return estado.enviando
                || estado.processandoFoto
                || estado.cameraAberta
                || estado.solicitandoPermissao;
    }

    private void atualizarControles() {
        boolean habilitado = !ocupado() && sessaoPronta;

        binding.btnSalvar.setEnabled(habilitado);
        binding.btnFoto.setEnabled(habilitado);
        binding.edtNome.setEnabled(habilitado);
        binding.edtEmail.setEnabled(habilitado);
        binding.edtSenha.setEnabled(habilitado);

        binding.progressoCadastro.setVisibility(
                ocupado() || !sessaoPronta ? View.VISIBLE : View.GONE
        );
    }

    private void apagarTemporario() {
        if (estado.caminhoCamera != null) {
            new File(estado.caminhoCamera).delete();
            estado.caminhoCamera = null;
        }
    }

    private void restaurarEstado(Bundle salvo) {
        if (!estado.restaurado) {
            estado.restaurado = true;

            if (salvo != null) {
                estado.foto = salvo.getByteArray("p2_foto");
                estado.fotoAlterada = salvo.getBoolean("p2_foto_alterada");
                estado.caminhoCamera = salvo.getString("p2_caminho_camera");
                estado.cameraAberta = salvo.getBoolean("p2_camera_aberta");
                estado.solicitandoPermissao =
                        salvo.getBoolean("p2_permissao_pendente");
                estado.preenchido = salvo.getBoolean("p2_preenchido");
                estado.usuarioId = salvo.getInt("p2_usuario_id");
            }
        }

        if (salvo != null) {
            binding.edtNome.setText(salvo.getString("p2_nome", ""));
            binding.edtEmail.setText(salvo.getString("p2_email", ""));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putByteArray("p2_foto", estado.foto);
        outState.putBoolean("p2_foto_alterada", estado.fotoAlterada);
        outState.putString("p2_caminho_camera", estado.caminhoCamera);
        outState.putBoolean("p2_camera_aberta", estado.cameraAberta);
        outState.putBoolean(
                "p2_permissao_pendente",
                estado.solicitandoPermissao
        );
        outState.putBoolean("p2_preenchido", estado.preenchido);
        outState.putInt("p2_usuario_id", estado.usuarioId);
        outState.putString("p2_nome", binding.edtNome.getText().toString());
        outState.putString("p2_email", binding.edtEmail.getText().toString());
    }

    @Override
    protected void onDestroy() {
        if (isFinishing()
                && !estado.processandoFoto
                && !estado.cameraAberta) {
            apagarTemporario();
        }

        super.onDestroy();
    }
}