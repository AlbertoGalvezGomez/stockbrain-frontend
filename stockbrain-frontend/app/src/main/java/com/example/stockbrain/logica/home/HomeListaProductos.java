package com.example.stockbrain.logica.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stockbrain.R;
import com.example.stockbrain.adaptador.ProductoAdapterAdmin;
import com.example.stockbrain.api.ApiClient;
import com.example.stockbrain.api.ApiService;
import com.example.stockbrain.logica.InicioSesion;
import com.example.stockbrain.modelo.Producto;
import com.example.stockbrain.modelo.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class HomeListaProductos extends AppCompatActivity {

    private RecyclerView recyclerViewProductos;
    private ProductoAdapterAdmin productoAdapter;
    private SessionManager sessionManager;

    private static final int REQUEST_EDITAR_PRODUCTO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_productos);

        sessionManager = new SessionManager(this);
        if (!sessionManager.estaLogueado()) {
            startActivity(new Intent(this, InicioSesion.class));
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mis Productos");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerViewProductos = findViewById(R.id.recyclerViewProductos);
        recyclerViewProductos.setLayoutManager(new LinearLayoutManager(this));


        productoAdapter = new ProductoAdapterAdmin();
        recyclerViewProductos.setAdapter(productoAdapter);

        productoAdapter.setOnEditarClickListener(producto -> {
            Intent intent = new Intent(HomeListaProductos.this, HomeEditarProducto.class);
            intent.putExtra("producto", producto);
            startActivityForResult(intent, REQUEST_EDITAR_PRODUCTO);
        });

        cargarProductosDeMiTienda();
    }

    private void cargarProductosDeMiTienda() {
        Long tiendaId = sessionManager.getTiendaId();
        if (tiendaId == null || tiendaId == 0L) {
            Toast.makeText(this, "No tienes una tienda asignada", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        api.obtenerProductosPorTienda(tiendaId).enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Producto> productos = response.body();

                    if (productos.isEmpty()) {
                        Toast.makeText(HomeListaProductos.this, "No hay productos aún", Toast.LENGTH_SHORT).show();
                    }

                    productoAdapter.setProductos(productos);
                } else {
                    Toast.makeText(HomeListaProductos.this, "Error del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Toast.makeText(HomeListaProductos.this, "Sin conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDITAR_PRODUCTO && resultCode == RESULT_OK) {
            cargarProductosDeMiTienda();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}