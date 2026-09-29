package com.example.avaliacao1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.avaliacao1.databinding.FragmentFirstBinding;

/**
 * TODO (Pessoa 3): Spinner alimentado por viewModel.getBiomas().
 * Ao selecionar: viewModel.selecionarBioma(bioma.id) ("Todos" = AppViewModel.TODOS).
 * ListView desta tela: viewModel.getItensFiltrados().
 * Código da Avaliação 1 para consulta: docs/legado_avaliacao1/
 */
public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private AppViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
        // TODO (Pessoa 3)
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
