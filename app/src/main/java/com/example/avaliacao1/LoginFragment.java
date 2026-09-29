package com.example.avaliacao1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.avaliacao1.databinding.FragmentLoginBinding;

/**
 * STUB FUNCIONAL da Pessoa 2 (melhore o visual e as validações).
 * Sucesso no login: NÃO navegue aqui. A MainActivity observa a sessão e troca de tela sozinha.
 */
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private AppViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        binding.btnEntrar.setOnClickListener(v -> {
            String email = binding.edtEmail.getText().toString();
            String senha = binding.edtSenha.getText().toString();
            viewModel.login(email, senha, r -> {
                if (!r.sucesso && isAdded()) {
                    Toast.makeText(requireContext(), r.mensagem, Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.btnCadastrar.setOnClickListener(v -> {
            Intent i = new Intent(requireContext(), CadastroActivity.class);
            i.putExtra(CadastroActivity.EXTRA_MODO, CadastroActivity.MODO_CADASTRO);
            startActivity(i);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
