
package com.example.stockbrain.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.stockbrain.R;
import com.example.stockbrain.modelo.Producto;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SeleccionProductoAdapter extends RecyclerView.Adapter<SeleccionProductoAdapter.ViewHolder> {

    private List<Producto> productos = new ArrayList<>();
    private List<Producto> productosFiltrados = new ArrayList<>();
    private OnProductoSeleccionadoListener listener;

    public interface OnProductoSeleccionadoListener {
        void onProductoSeleccionado(Producto producto);
    }

    public SeleccionProductoAdapter(OnProductoSeleccionadoListener listener) {
        this.listener = listener;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = new ArrayList<>(productos);
        this.productosFiltrados = new ArrayList<>(productos);
        notifyDataSetChanged();
    }

    public void filtrar(String query) {
        query = query.toLowerCase(Locale.getDefault());
        productosFiltrados.clear();
        if (query.isEmpty()) {
            productosFiltrados.addAll(productos);
        } else {
            for (Producto p : productos) {
                if (p.getNombre().toLowerCase(Locale.getDefault()).contains(query)) {
                    productosFiltrados.add(p);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_seleccion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto p = productosFiltrados.get(position);

        holder.tvNombre.setText(p.getNombre());
        holder.tvPrecio.setText(String.format("Precio: $%.2f", p.getPrecio()));
        holder.tvStock.setText("Stock: " + p.getStock());

        Glide.with(holder.itemView.getContext())
                .load(p.getImagenUrl())
                .placeholder(R.drawable.hide_image)
                .error(R.drawable.hide_image)
                .centerCrop()
                .into(holder.imgProducto);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductoSeleccionado(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productosFiltrados.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProducto;
        TextView tvNombre, tvPrecio, tvStock;

        ViewHolder(View itemView) {
            super(itemView);
            imgProducto = itemView.findViewById(R.id.imgProducto);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvStock = itemView.findViewById(R.id.tvStock);
        }
    }
}