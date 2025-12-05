package com.example.stockbrain.logica;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockbrain.R;
import com.example.stockbrain.adaptador.ProductoAdapterUsuarios;
import com.example.stockbrain.api.ApiClient;
import com.example.stockbrain.api.ApiService;
import com.example.stockbrain.modelo.Producto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaProductosUsuario extends AppCompatActivity {

    private RecyclerView recyclerViewProductos;
    private ProductoAdapterUsuarios productoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_productos_usuarios);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Productos");
        }

        recyclerViewProductos = findViewById(R.id.recyclerViewProductos);
        recyclerViewProductos.setLayoutManager(new LinearLayoutManager(this));
        productoAdapter = new ProductoAdapterUsuarios(new ArrayList<>());
        recyclerViewProductos.setAdapter(productoAdapter);

        Long tiendaId = getIntent().getLongExtra("tiendaId", 0L);
        if (tiendaId == 0L) {
            Toast.makeText(this, "Error: tienda no válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cargarProductos(tiendaId);
    }

    private void cargarProductos(Long tiendaId) {
        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        api.obtenerProductosPorTienda(tiendaId).enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productoAdapter.setProductos(response.body());
                    if (response.body().isEmpty()) {
                        Toast.makeText(ListaProductosUsuario.this, "No hay productos disponibles", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ListaProductosUsuario.this, "Error al cargar productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Toast.makeText(ListaProductosUsuario.this, "Sin conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}