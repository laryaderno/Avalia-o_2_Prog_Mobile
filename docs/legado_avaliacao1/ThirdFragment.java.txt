package com.example.avaliacao1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.avaliacao1.databinding.FragmentThirdBinding;

import java.util.ArrayList;
import java.util.List;

public class ThirdFragment extends Fragment {

    private FragmentThirdBinding binding;
    private BiomaViewModel biomaViewModel;
    private Bioma biomaAtual;
    private List<Bioma> listaBiomas;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentThirdBinding.inflate(
                inflater,
                container,
                false
        );

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        biomaViewModel =
                new ViewModelProvider(requireActivity())
                        .get(BiomaViewModel.class);

        listaBiomas = montarListaBiomas();

        biomaViewModel.getBiomaSelecionado().observe(
                getViewLifecycleOwner(),
                bioma -> {

                    biomaAtual = bioma;

                    if (bioma == null) {

                        binding.info.setText("Galeria de Biomas");

                        mostrarTodosOsBiomas();

                    } else {

                        binding.info.setText(
                                getString(
                                        R.string.galeria_do_bioma,
                                        bioma.getNome()
                                )
                        );

                        mostrarBiomaSelecionado(bioma);
                    }
                }
        );

        // Clique no item do GridView
        binding.grid.setOnItemClickListener(
                (parent, viewClicada, position, id) -> {

                    Bioma biomaSelecionado =
                            (Bioma) parent.getItemAtPosition(position);

                    abrirDetalhe(
                            biomaSelecionado.getNome(),
                            biomaSelecionado.getDescricao(),
                            biomaSelecionado.getImagemPrincipal(),
                            biomaSelecionado.getSom()
                    );
                }
        );

        // Esconde/mostra a BottomNavigation conforme o scroll
        binding.grid.setOnScrollListener(
                new AbsListView.OnScrollListener() {

                    private int ultimaPosicao = 0;

                    @Override
                    public void onScrollStateChanged(
                            AbsListView view,
                            int scrollState
                    ) {
                    }

                    @Override
                    public void onScroll(
                            AbsListView view,
                            int firstVisibleItem,
                            int visibleItemCount,
                            int totalItemCount
                    ) {

                        if (firstVisibleItem > ultimaPosicao) {

                            ((MainActivity) requireActivity())
                                    .esconderBottomNavigation();

                        } else if (firstVisibleItem < ultimaPosicao) {

                            ((MainActivity) requireActivity())
                                    .mostrarBottomNavigation();
                        }

                        ultimaPosicao = firstVisibleItem;
                    }
                }
        );
    }

    private void mostrarTodosOsBiomas() {

        Adapter adapter =
                new Adapter(
                        requireContext(),
                        listaBiomas
                );

        binding.grid.setAdapter(adapter);
    }

    private void mostrarBiomaSelecionado(Bioma bioma) {

        List<Bioma> lista = new ArrayList<>();

        lista.add(bioma);

        Adapter adapter =
                new Adapter(
                        requireContext(),
                        lista
                );

        binding.grid.setAdapter(adapter);
    }

    private List<Bioma> montarListaBiomas() {

        List<Bioma> lista = new ArrayList<>();

        lista.add(
                new Bioma(
                        getString(R.string.bioma_amazonia),
                        getString(R.string.desc_amazonia),
                        R.drawable.amazonia_img1,
                        R.drawable.amazonia_img2,
                        R.raw.som_amazonia
                )
        );

        lista.add(
                new Bioma(
                        getString(R.string.bioma_caatinga),
                        getString(R.string.desc_caatinga),
                        R.drawable.caatinga_img1,
                        R.drawable.caatinga_img2,
                        R.raw.som_caatinga
                )
        );

        lista.add(
                new Bioma(
                        getString(R.string.bioma_cerrado),
                        getString(R.string.desc_cerrado),
                        R.drawable.cerrado_img1,
                        R.drawable.cerrado_img2,
                        R.raw.som_cerrado
                )
        );

        lista.add(
                new Bioma(
                        getString(R.string.bioma_mata_atlantica),
                        getString(R.string.desc_mata_atlantica),
                        R.drawable.mata_atlantica_img1,
                        R.drawable.mata_atlantica_img2,
                        R.raw.som_mata_atlantica
                )
        );

        lista.add(
                new Bioma(
                        getString(R.string.bioma_pampa),
                        getString(R.string.desc_pampa),
                        R.drawable.pampa_img1,
                        R.drawable.pampa_img2,
                        R.raw.som_pampa
                )
        );

        lista.add(
                new Bioma(
                        getString(R.string.bioma_pantanal),
                        getString(R.string.desc_pantanal),
                        R.drawable.pantanal_img1,
                        R.drawable.pantanal_img2,
                        R.raw.som_pantanal
                )
        );

        return lista;
    }

    private void abrirDetalhe(
            String titulo,
            String descricao,
            int imagem,
            int som
    ) {

        Intent intent =
                new Intent(
                        requireContext(),
                        DetalheActivity.class
                );

        intent.putExtra(
                DetalheActivity.EXTRA_TITULO,
                titulo
        );

        intent.putExtra(
                DetalheActivity.EXTRA_DESCRICAO,
                descricao
        );

        intent.putExtra(
                DetalheActivity.EXTRA_IMAGEM,
                imagem
        );

        intent.putExtra(
                DetalheActivity.EXTRA_SOM,
                som
        );

        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}