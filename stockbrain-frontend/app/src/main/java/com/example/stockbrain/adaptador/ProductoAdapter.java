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

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private List<Producto> productos = new ArrayList<>();  // ← Ahora inicializado

    public ProductoAdapter() {
        // Constructor vacío (más cómodo para usar desde Activity)
    }

    // Constructor opcional si quieres pasarle una lista al crear
    public ProductoAdapter(List<Producto> productos) {
        this.productos = productos != null ? productos : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto p = productos.get(position);
        holder.tvNombre.setText(p.getNombre());
        holder.tvPrecio.setText(String.format("Precio: $%.2f", p.getPrecio()));
        holder.tvStock.setText("Stock: " + p.getStock());

        // Carga segura de imagen
        String imagenUrl = p.getImagenUrl();
        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imagenUrl)
                    .placeholder(R.drawable.hide_image)
                    .error(R.drawable.hide_image)
                    .centerCrop()
                    .into(holder.imgProducto);
        } else {
            holder.imgProducto.setImageResource(R.drawable.hide_image);
        }
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    // MÉTODO CLAVE: para actualizar la lista desde la Activity
    public void setProductos(List<Producto> nuevosProductos) {
        this.productos.clear();
        if (nuevosProductos != null) {
            this.productos.addAll(nuevosProductos);
        }
        notifyDataSetChanged();
    }

    // Bonus: método para añadir un producto (útil si agregas uno nuevo)
    public void agregarProducto(Producto producto) {
        if (producto != null) {
            productos.add(0, producto);  // lo pone arriba
            notifyItemInserted(0);
        }
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