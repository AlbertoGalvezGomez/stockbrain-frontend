package com.example.stockbrain.logica.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stockbrain.R;
import com.example.stockbrain.adaptador.ProductoAdapter;  // ← Crea este adapter si no existe
import com.example.stockbrain.api.ApiClient;
import com.example.stockbrain.api.ApiService;
import com.example.stockbrain.modelo.Producto;
import com.example.stockbrain.modelo.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class HomeListaProductos extends AppCompatActivity {

    private static final String TAG = "ListaProductosTienda";

    private RecyclerView recyclerViewProductos;
    private ProductoAdapter productoAdapter;  // ← Nuevo adapter para productos
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_productos);  // ← Cambia al layout que uses

        sessionManager = new SessionManager(this);

        // Solo usuarios logueados (USER o ADMIN pueden ver sus productos)
        if (!sessionManager.estaLogueado()) {
            startActivity(new Intent(this, com.example.stockbrain.logica.InicioSesion.class));
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mis Productos");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerViewProductos = findViewById(R.id.recyclerViewProductos); // ← ID del RecyclerView
        recyclerViewProductos.setLayoutManager(new LinearLayoutManager(this));

        productoAdapter = new ProductoAdapter(new ArrayList<>());
        recyclerViewProductos.setAdapter(productoAdapter);

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
                        Toast.makeText(HomeListaProductos.this, "No hay productos en esta tienda", Toast.LENGTH_SHORT).show();
                    }
                    productoAdapter.setProductos(productos);
                } else {
                    Toast.makeText(HomeListaProductos.this, "Error al cargar productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Log.e(TAG, "Error de red", t);
                Toast.makeText(HomeListaProductos.this, "Sin conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}