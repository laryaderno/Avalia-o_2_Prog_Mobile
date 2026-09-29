package com.example.avaliacao1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;

public class BiomaListAdapter extends BaseAdapter {

    private final Context context;
    private final List<Bioma> biomas;

    public BiomaListAdapter(Context context, List<Bioma> biomas) {
        this.context = context;
        this.biomas = biomas;
    }

    @Override
    public int getCount() {
        return biomas.size();
    }

    @Override
    public Object getItem(int position) {
        return biomas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, @Nullable View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_bioma_lista, parent, false);
            holder = new ViewHolder();
            holder.imagem = convertView.findViewById(R.id.imgItemBioma);
            holder.titulo = convertView.findViewById(R.id.txtItemTitulo);
            holder.descricao = convertView.findViewById(R.id.txtItemDescricao);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Bioma bioma = biomas.get(position);
        holder.imagem.setImageResource(bioma.getImagemPrincipal());
        holder.titulo.setText(bioma.getNome());
        holder.descricao.setText(bioma.getDescricao());

        return convertView;
    }

    private static class ViewHolder {
        ImageView imagem;
        TextView titulo;
        TextView descricao;
    }
}