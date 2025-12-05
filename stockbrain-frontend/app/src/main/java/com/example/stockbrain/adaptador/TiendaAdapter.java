package com.example.stockbrain.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockbrain.R;
import com.example.stockbrain.modelo.Tienda;

import java.util.List;

public class TiendaAdapter extends RecyclerView.Adapter<TiendaAdapter.TiendaViewHolder> {

    private List<Tienda> tiendas;
    private OnTiendaClickListener listener;

    public TiendaAdapter(List<Tienda> tiendas) {
        this.tiendas = tiendas;
    }

    public interface OnTiendaClickListener {
        void onTiendaClick(Tienda tienda);
    }

    public void setOnTiendaClickListener(OnTiendaClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TiendaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tienda, parent, false);
        return new TiendaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TiendaViewHolder holder, int position) {
        Tienda tienda = tiendas.get(position);
        holder.textNombreTienda.setText(tienda.getNombre());
        holder.textUbicacionTienda.setText(tienda.getUbicacion());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTiendaClick(tienda);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tiendas != null ? tiendas.size() : 0;
    }

    public void setTiendas(List<Tienda> tiendas) {
        this.tiendas = tiendas;
        notifyDataSetChanged();
    }

    static class TiendaViewHolder extends RecyclerView.ViewHolder {
        TextView textNombreTienda, textUbicacionTienda;

        TiendaViewHolder(@NonNull View itemView) {
            super(itemView);
            textNombreTienda = itemView.findViewById(R.id.textNombreTienda);
            textUbicacionTienda = itemView.findViewById(R.id.textUbicacionTienda);
        }
    }
}