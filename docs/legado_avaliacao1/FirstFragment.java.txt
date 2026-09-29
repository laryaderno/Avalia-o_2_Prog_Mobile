package com.example.avaliacao1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.avaliacao1.databinding.FragmentFirstBinding;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private BiomaViewModel biomaViewModel;
    private List<Bioma> listaBiomas;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        biomaViewModel = new ViewModelProvider(requireActivity()).get(BiomaViewModel.class);
        listaBiomas = montarListaBiomas();

        // Spinner
        String[] nomes = new String[listaBiomas.size() + 1];

        nomes[0] = "Selecione";

        for (int i = 0; i < listaBiomas.size(); i++) {
            nomes[i + 1] = listaBiomas.get(i).getNome();
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                nomes
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerBiomas.setAdapter(spinnerAdapter);

        // ListView com adapter personalizado (imagem + título + descrição)
        BiomaListAdapter listAdapter = new BiomaListAdapter(requireContext(), listaBiomas);
        binding.listViewBiomas.setAdapter(listAdapter);

        // Ao trocar o Spinner -> avisa o ViewModel -> Fragmentos 2 e 3 escutam e atualizam
        binding.spinnerBiomas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    biomaViewModel.selecionarBioma(null);
                } else {
                    biomaViewModel.selecionarBioma(listaBiomas.get(position - 1));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Já manda o primeiro bioma pro ViewModel assim que a tela abre
        if (!listaBiomas.isEmpty()) {
            biomaViewModel.selecionarBioma(listaBiomas.get(0));
        }

        // Clique num item do ListView abre a Activity Extra
        binding.listViewBiomas.setOnItemClickListener((parent, itemView, position, id) -> {
            Bioma bioma = listaBiomas.get(position);
            Intent intent = new Intent(requireContext(), DetalheActivity.class);
            intent.putExtra(DetalheActivity.EXTRA_TITULO, bioma.getNome());
            intent.putExtra(DetalheActivity.EXTRA_DESCRICAO, bioma.getDescricao());
            intent.putExtra(DetalheActivity.EXTRA_IMAGEM, bioma.getImagemPrincipal());
            intent.putExtra(DetalheActivity.EXTRA_SOM, bioma.getSom());
            startActivity(intent);
        });
    }

    private List<Bioma> montarListaBiomas() {
        List<Bioma> lista = new ArrayList<>();

        lista.add(new Bioma(getString(R.string.bioma_amazonia), getString(R.string.desc_amazonia),
                R.drawable.amazonia_img1, R.drawable.amazonia_img2,
                R.raw.som_amazonia));

        lista.add(new Bioma(getString(R.string.bioma_caatinga), getString(R.string.desc_caatinga),
                R.drawable.caatinga_img1, R.drawable.caatinga_img2, R.raw.som_caatinga));

        lista.add(new Bioma(getString(R.string.bioma_cerrado), getString(R.string.desc_cerrado),
                R.drawable.cerrado_img1, R.drawable.cerrado_img2,
                R.raw.som_cerrado));

        lista.add(new Bioma(getString(R.string.bioma_mata_atlantica), getString(R.string.desc_mata_atlantica),
                R.drawable.mata_atlantica_img1, R.drawable.mata_atlantica_img2,
                R.raw.som_mata_atlantica));

        lista.add(new Bioma(getString(R.string.bioma_pampa), getString(R.string.desc_pampa),
                R.drawable.pampa_img1, R.drawable.pampa_img2, R.raw.som_pampa));

        lista.add(new Bioma(getString(R.string.bioma_pantanal), getString(R.string.desc_pantanal),
                R.drawable.pantanal_img1, R.drawable.pantanal_img2, R.raw.som_pantanal));

        return lista;
    }

    private String[] nomesDosBiomas(List<Bioma> lista) {
        String[] nomes = new String[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            nomes[i] = lista.get(i).getNome();
        }
        return nomes;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}