package com.example.stockbrain.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class ProductoAdapterAdmin extends RecyclerView.Adapter<ProductoAdapterAdmin.ViewHolder> {

    private List<Producto> productos = new ArrayList<>();
    private OnEditarClickListener listener;


    public interface OnEditarClickListener {
        void onEditarClick(Producto producto);
    }

    public void setOnEditarClickListener(OnEditarClickListener listener) {
        this.listener = listener;
    }

    public ProductoAdapterAdmin() {
    }

    public ProductoAdapterAdmin(List<Producto> productos) {
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
        holder.tvPrecio.setText(String.format("$%.2f", p.getPrecio()));
        holder.tvStock.setText("Stock: " + p.getStock());

        String url = p.getImagenUrl();

        if (url != null && !url.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .placeholder(R.drawable.hide_image)
                    .error(R.drawable.hide_image)
                    .centerCrop()
                    .override(500, 500)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(holder.imgProducto);
        } else {
            holder.imgProducto.setImageResource(R.drawable.hide_image);
        }

        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) listener.onEditarClick(p);
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public void setProductos(List<Producto> nuevosProductos) {
        this.productos.clear();
        if (nuevosProductos != null) {
            this.productos.addAll(nuevosProductos);
        }
        notifyDataSetChanged();
    }

    public void agregarProducto(Producto producto) {
        if (producto != null) {
            productos.add(0, producto);
            notifyItemInserted(0);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProducto;
        TextView tvNombre, tvPrecio, tvStock;
        ImageButton btnEditar;

        ViewHolder(View itemView) {
            super(itemView);
            imgProducto = itemView.findViewById(R.id.imgProducto);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvStock = itemView.findViewById(R.id.tvStock);
            btnEditar = itemView.findViewById(R.id.btn_edit_producto);
        }
    }
}