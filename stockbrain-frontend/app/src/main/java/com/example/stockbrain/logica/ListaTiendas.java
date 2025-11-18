package com.example.stockbrain.logica;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.stockbrain.R;
import com.example.stockbrain.adaptador.TiendaAdapter;
import com.example.stockbrain.api.ApiClient;
import com.example.stockbrain.api.ApiService;
import com.example.stockbrain.modelo.SessionManager;
import com.example.stockbrain.modelo.Tienda;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class ListaTiendas extends AppCompatActivity {

    private static final String TAG = "ListaTiendas";

    private RecyclerView recyclerViewTiendas;
    private TiendaAdapter tiendaAdapter;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_tiendas);

        sessionManager = new SessionManager(this);

        if (!sessionManager.estaLogueado() || !"USER".equalsIgnoreCase(sessionManager.getRol())) {
            Toast.makeText(this, "Debes iniciar sesión como usuario", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, com.example.stockbrain.logica.InicioSesion.class));
            finish();
            return;
        }

        Long userId = sessionManager.getUserId();

        if (userId == null || userId == 0L) {
            Toast.makeText(this, "Error de sesión", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, com.example.stockbrain.logica.InicioSesion.class));
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Elige tu tienda");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerViewTiendas = findViewById(R.id.recyclerViewTiendas);
        recyclerViewTiendas.setLayoutManager(new LinearLayoutManager(this));
        tiendaAdapter = new TiendaAdapter(new ArrayList<>());
        recyclerViewTiendas.setAdapter(tiendaAdapter);

        cargarTiendas(userId);
    }

    private void cargarTiendas(Long userId) {
        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        api.obtenerTiendas(userId).enqueue(new Callback<List<Tienda>>() {
            @Override
            public void onResponse(Call<List<Tienda>> call, Response<List<Tienda>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tiendaAdapter.setTiendas(response.body());
                } else {
                    Toast.makeText(ListaTiendas.this, "No hay tiendas disponibles", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Tienda>> call, Throwable t) {
                Toast.makeText(ListaTiendas.this, "Sin conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}