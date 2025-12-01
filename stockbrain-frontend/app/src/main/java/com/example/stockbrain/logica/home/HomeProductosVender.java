package com.example.stockbrain.logica.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockbrain.R;
import com.example.stockbrain.adaptador.SeleccionProductoAdapter;
import com.example.stockbrain.api.ApiClient;
import com.example.stockbrain.api.ApiService;
import com.example.stockbrain.modelo.Producto;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeProductosVender extends AppCompatActivity {

    private RecyclerView recycler;
    private TextInputEditText etBuscar;
    private ProgressBar progressBar;
    private SeleccionProductoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_productos_vender);

        recycler = findViewById(R.id.recyclerProductos);
        etBuscar = findViewById(R.id.etBuscarProducto);
        progressBar = findViewById(R.id.progressBar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Productos a vender");
        }

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SeleccionProductoAdapter(producto -> {
            Intent result = new Intent();
            result.putExtra("producto", producto);
            setResult(RESULT_OK, result);
            finish();
        });
        recycler.setAdapter(adapter);

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                adapter.filtrar(s.toString());
            }
        });

        cargarProductos();
    }

    private void cargarProductos() {
        progressBar.setVisibility(View.VISIBLE);

        // Leer el tienda_id del usuario logueado (el mismo que usas en VentasActivity)
        SharedPreferences prefs = getSharedPreferences("data_login", MODE_PRIVATE);
        long tiendaId = prefs.getLong("tienda_id", 0L);

        if (tiendaId == 0L) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "No tienes tienda asignada", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        api.obtenerProductosPorTienda(tiendaId).enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setProductos(response.body());
                } else {
                    Toast.makeText(HomeProductosVender.this, "No se pudieron cargar los productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(HomeProductosVender.this, "Error de conexión", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}