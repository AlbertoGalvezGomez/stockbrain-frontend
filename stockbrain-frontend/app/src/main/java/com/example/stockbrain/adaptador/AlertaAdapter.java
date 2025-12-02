package com.example.stockbrain.adaptador;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockbrain.R;
import com.example.stockbrain.modelo.Alerta;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlertaAdapter extends RecyclerView.Adapter<AlertaAdapter.ViewHolder> {
    private List<Alerta> lista = new ArrayList<>();

    public void setLista(List<Alerta> lista) {
        this.lista = lista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alerta, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int p) {
        Alerta a = lista.get(p);

        String titulo;
        switch (a.getTipo()) {
            case "VENTA_REALIZADA":
                titulo = "Venta realizada";
                break;
            case "NUEVO_PRODUCTO":
                titulo = "Producto nuevo";
                break;
            case "PRODUCTO_EDITADO":
                titulo = "Producto editado";
                break;
            case "PRODUCTO_ELIMINADO":
                titulo = "Producto eliminado";
                break;
            default:
                titulo = "Actividad";
                break;
        }

        int icono = 0;
        int colorFondo = 0;
        switch (a.getTipo()) {
            case "VENTA_REALIZADA":
                icono = R.drawable.ic_venta;
                colorFondo = Color.parseColor("#4CAF50");
                break;
            case "NUEVO_PRODUCTO":
                icono = R.drawable.ic_nuevo;
                colorFondo = Color.parseColor("#2196F3");
                break;
            case "PRODUCTO_EDITADO":
                icono = R.drawable.ic_editar;
                colorFondo = Color.parseColor("#FF9800");
                break;
            case "PRODUCTO_ELIMINADO":
                icono = R.drawable.ic_eliminar;
                colorFondo = Color.parseColor("#F44336");
                break;
        }

        h.ivIcono.setImageResource(icono);
        h.ivIcono.setBackgroundTintList(ColorStateList.valueOf(colorFondo));
        h.tvTitulo.setText(titulo);
        h.tvMensaje.setText(a.getMensaje());

        // Formato fecha
        String fechaRaw = a.getFecha().substring(0, 10);
        String hoy = LocalDate.now().toString();
        String ayer = LocalDate.now().minusDays(1).toString();
        String fechaMostrar = fechaRaw.equals(hoy) ? "Hoy" :
                fechaRaw.equals(ayer) ? "Ayer" : fechaRaw;
        h.tvFecha.setText(fechaMostrar);
    }

    @Override public int getItemCount() { return lista.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcono;
        TextView tvTitulo, tvMensaje, tvFecha;
        ViewHolder(View v) {
            super(v);
            ivIcono = v.findViewById(R.id.ivIcono);
            tvTitulo = v.findViewById(R.id.tvTitulo);
            tvMensaje = v.findViewById(R.id.tvMensaje);
            tvFecha = v.findViewById(R.id.tvFecha);
        }
    }
}