package com.example.avaliacao1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.avaliacao1.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AppViewModel viewModel;
    private EstadoLogin estado;

    public static class EstadoLogin extends ViewModel {
        boolean enviando;
        final MutableLiveData<Resultado<Usuario>> resultado = new MutableLiveData<>();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        estado = new ViewModelProvider(this).get(EstadoLogin.class);

        estado.resultado.observe(getViewLifecycleOwner(), resultado -> {
            if (resultado == null) return;

            estado.resultado.setValue(null);
            atualizarCarregamento();

            if (!resultado.sucesso && isAdded()) {
                Toast.makeText(
                        requireContext(),
                        resultado.mensagem,
                        Toast.LENGTH_LONG
                ).show();
            }
        });

        binding.btnEntrar.setOnClickListener(v -> entrar());

        binding.edtSenha.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                entrar();
                return true;
            }
            return false;
        });

        binding.btnCadastrar.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CadastroActivity.class);
            intent.putExtra(
                    CadastroActivity.EXTRA_MODO,
                    CadastroActivity.MODO_CADASTRO
            );
            startActivity(intent);
        });

        atualizarCarregamento();
    }

    private void entrar() {
        if (binding == null || estado.enviando) return;

        String email = binding.edtEmail.getText().toString().trim();
        String senha = binding.edtSenha.getText().toString();

        binding.edtEmail.setError(null);
        binding.edtSenha.setError(null);

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.setError(getString(R.string.p2_email_invalido));
            binding.edtEmail.requestFocus();
            return;
        }

        if (senha.trim().isEmpty()) {
            binding.edtSenha.setError(getString(R.string.p2_senha_obrigatoria));
            binding.edtSenha.requestFocus();
            return;
        }

        estado.enviando = true;
        atualizarCarregamento();

        EstadoLogin operacao = estado;

        viewModel.login(email, senha, resultado -> {
            operacao.enviando = false;
            operacao.resultado.setValue(resultado);
        });
    }

    private void atualizarCarregamento() {
        if (binding == null) return;

        binding.btnEntrar.setEnabled(!estado.enviando);
        binding.btnCadastrar.setEnabled(!estado.enviando);
        binding.edtEmail.setEnabled(!estado.enviando);
        binding.edtSenha.setEnabled(!estado.enviando);

        binding.progressoLogin.setVisibility(
                estado.enviando ? View.VISIBLE : View.GONE
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}