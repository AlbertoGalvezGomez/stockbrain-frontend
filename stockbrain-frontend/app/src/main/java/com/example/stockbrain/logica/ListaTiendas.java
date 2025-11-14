package com.example.stockbrain.logica;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_tiendas);

        SharedPreferences prefs = getSharedPreferences("data_login", MODE_PRIVATE);
        String userIdStr = prefs.getString("user_id", null);
        String rol = prefs.getString("rol", null);

        if (userIdStr == null || !"USER".equalsIgnoreCase(rol)) {
            Log.e(TAG, "Acceso denegado: user_id=" + userIdStr + ", rol=" + rol);
            Toast.makeText(this, "Debes iniciar sesión como usuario", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, InicioSesion.class));
            finish();
            return;
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "ID inválido: " + userIdStr);
            Toast.makeText(this, "Error interno", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, InicioSesion.class));
            finish();
            return;
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lista de Tiendas");
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
        Call<List<Tienda>> call = api.obtenerTiendas(userId);

        call.enqueue(new Callback<List<Tienda>>() {
            @Override
            public void onResponse(Call<List<Tienda>> call, Response<List<Tienda>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Tienda> tiendas = response.body();
                    Log.d(TAG, "Tiendas cargadas: " + tiendas.size());
                    tiendaAdapter.setTiendas(tiendas);
                } else {
                    Log.e(TAG, "Error HTTP: " + response.code());
                    Toast.makeText(ListaTiendas.this, "Error al cargar tiendas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Tienda>> call, Throwable t) {
                Log.e(TAG, "Error de red: " + t.getMessage());
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