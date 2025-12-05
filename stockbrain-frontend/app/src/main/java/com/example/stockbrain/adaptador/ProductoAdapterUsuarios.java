package com.example.stockbrain.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.stockbrain.R;
import com.example.stockbrain.modelo.Producto;

import java.util.ArrayList;
import java.util.List;

public class ProductoAdapterUsuarios extends RecyclerView.Adapter<ProductoAdapterUsuarios.ProductoViewHolder> {

    private List<Producto> productos = new ArrayList<>();

    public ProductoAdapterUsuarios() {}

    public ProductoAdapterUsuarios(List<Producto> productos) {
        this.productos = productos != null ? productos : new ArrayList<>();
    }

    public void setProductos(List<Producto> productos) {
        this.productos.clear();
        if (productos != null) this.productos.addAll(productos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_usuarios, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto p = productos.get(position);

        holder.tvNombre.setText(p.getNombre());
        holder.tvPrecio.setText(String.format("%.2f €", p.getPrecio()));
        holder.tvStock.setText("Stock: " + p.getStock());

        String url = p.getImagenUrl();
        if (url != null && !url.trim().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .placeholder(R.drawable.hide_image)
                    .error(R.drawable.hide_image)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(holder.imgProducto);
        } else {
            holder.imgProducto.setImageResource(R.drawable.hide_image);
        }
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    static class ProductoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProducto;
        TextView tvNombre, tvPrecio, tvStock, tvStockBajo;

        ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProducto = itemView.findViewById(R.id.imgProducto);
            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvStockBajo = itemView.findViewById(R.id.tvStockBajo);
        }
    }
}