package com.example.avaliacao1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.avaliacao1.databinding.FragmentThirdBinding;

import java.util.ArrayList;
import java.util.List;

public class ThirdFragment extends Fragment {

    private FragmentThirdBinding binding;
    private AppViewModel viewModel;

    private ArrayAdapter<Item> adapter;
    private final List<Item> itens = new ArrayList<>();

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

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
            Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity())
                .get(AppViewModel.class);

        configurarGrid();
        observarItens();
        configurarScroll();
    }

    private void configurarGrid() {

        adapter = new ArrayAdapter<Item>(
                requireContext(),
                R.layout.item_grid,
                R.id.textNomeItemGrid,
                itens
        ) {

            @NonNull
            @Override
            public View getView(
                    int position,
                    View convertView,
                    @NonNull ViewGroup parent) {

                View row = super.getView(
                        position,
                        convertView,
                        parent
                );

                Item item = getItem(position);

                ImageView imageView =
                        row.findViewById(R.id.imageItemGrid);

                if (item != null &&
                        item.getCaminhoImagem() != null &&
                        !item.getCaminhoImagem().isEmpty()) {

                    int imagemId = getResources().getIdentifier(
                            item.getCaminhoImagem(),
                            "drawable",
                            requireContext().getPackageName()
                    );

                    if (imagemId != 0) {
                        imageView.setImageResource(imagemId);
                    } else {
                        imageView.setImageResource(
                                android.R.drawable.ic_menu_gallery
                        );
                    }

                } else {

                    imageView.setImageResource(
                            android.R.drawable.ic_menu_gallery
                    );
                }

                return row;
            }
        };

        binding.grid.setAdapter(adapter);

        binding.grid.setOnItemClickListener(
                (parent, view, position, id) -> {

                    Item item = adapter.getItem(position);

                    if (item != null) {
                        abrirDetalhe(item);
                    }
                }
        );
    }

    private void observarItens() {

        viewModel.getItensFiltrados().observe(
                getViewLifecycleOwner(),
                novosItens -> {

                    itens.clear();

                    if (novosItens != null) {
                        itens.addAll(novosItens);
                    }

                    adapter.notifyDataSetChanged();
                }
        );
    }

    private void abrirDetalhe(Item item) {

        viewModel.getBioma(item.getBiomaId()).observe(
                getViewLifecycleOwner(),
                bioma -> {

                    if (bioma == null || !isAdded()) {
                        return;
                    }

                    Intent intent = new Intent(
                            requireContext(),
                            DetalheActivity.class
                    );

                    intent.putExtra(
                            DetalheActivity.EXTRA_TITULO,
                            item.getNome()
                    );

                    intent.putExtra(
                            DetalheActivity.EXTRA_DESCRICAO,
                            bioma.getDescricao()
                    );

                    int imagemId = 0;

                    if (item.getCaminhoImagem() != null &&
                            !item.getCaminhoImagem().isEmpty()) {

                        imagemId = getResources().getIdentifier(
                                item.getCaminhoImagem(),
                                "drawable",
                                requireContext().getPackageName()
                        );
                    }

                    intent.putExtra(
                            DetalheActivity.EXTRA_IMAGEM,
                            imagemId
                    );

                    int somId = 0;

                    if (bioma.getCaminhoAudio() != null &&
                            !bioma.getCaminhoAudio().isEmpty()) {

                        somId = getResources().getIdentifier(
                                bioma.getCaminhoAudio(),
                                "raw",
                                requireContext().getPackageName()
                        );
                    }

                    intent.putExtra(
                            DetalheActivity.EXTRA_SOM,
                            somId
                    );

                    startActivity(intent);
                }
        );
    }

    private void configurarScroll() {

        binding.grid.setOnScrollListener(
                new AbsListView.OnScrollListener() {

                    private int ultimoPrimeiroItem = 0;

                    @Override
                    public void onScrollStateChanged(
                            AbsListView view,
                            int scrollState) {
                    }

                    @Override
                    public void onScroll(
                            AbsListView view,
                            int firstVisibleItem,
                            int visibleItemCount,
                            int totalItemCount) {

                        MainActivity activity =
                                (MainActivity) requireActivity();

                        if (firstVisibleItem > ultimoPrimeiroItem) {

                            activity.esconderBottomNavigation();

                        } else if (firstVisibleItem < ultimoPrimeiroItem) {

                            activity.mostrarBottomNavigation();
                        }

                        if (firstVisibleItem == 0) {
                            activity.mostrarBottomNavigation();
                        }

                        ultimoPrimeiroItem = firstVisibleItem;
                    }
                }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}