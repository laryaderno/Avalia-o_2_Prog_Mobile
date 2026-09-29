package com.example.avaliacao1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.avaliacao1.databinding.FragmentSecondBinding;

/**
 * TODO (Pessoa 3): ListView (binding.listBiomas) alimentado por viewModel.getItensFiltrados().
 * Clique no item -> DetalheActivity. Rolagem -> ((MainActivity) requireActivity())
 * .esconderBottomNavigation() / .mostrarBottomNavigation() (já existem na MainActivity).
 */
public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private AppViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
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
