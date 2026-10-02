package com.example.avaliacao1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.avaliacao1.databinding.FragmentFirstBinding;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private AppViewModel viewModel;

    private ArrayAdapter<String> adapter;
    private final List<String> nomesBiomas = new ArrayList<>();
    private final List<Integer> idsBiomas = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity())
                .get(AppViewModel.class);

        configurarSpinner();
        observarBiomas();
    }

    private void configurarSpinner() {

        adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                nomesBiomas
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        binding.spinnerBiomas.setAdapter(adapter);

        binding.spinnerBiomas.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        if (position >= 0 && position < idsBiomas.size()) {
                            viewModel.selecionarBioma(idsBiomas.get(position));
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent) {

                        viewModel.selecionarBioma(AppViewModel.TODOS);
                    }
                }
        );
    }

    private void observarBiomas() {

        viewModel.getBiomas().observe(
                getViewLifecycleOwner(),
                biomas -> {

                    nomesBiomas.clear();
                    idsBiomas.clear();

                    // Opção "Todos"
                    nomesBiomas.add("Todos");
                    idsBiomas.add(AppViewModel.TODOS);

                    if (biomas != null) {
                        for (Bioma bioma : biomas) {
                            nomesBiomas.add(bioma.getNome());
                            idsBiomas.add(bioma.getId());
                        }
                    }

                    adapter.notifyDataSetChanged();
                }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}