package com.example.stockbrain.logica.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.stockbrain.R;
import com.example.stockbrain.adaptador.AlertaAdapter;
import com.example.stockbrain.api.ApiClient;
import com.example.stockbrain.api.ApiService;
import com.example.stockbrain.modelo.Alerta;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeAlertas extends AppCompatActivity {

    private RecyclerView recycler;
    private SwipeRefreshLayout swipe;
    private ProgressBar progress;
    private AlertaAdapter adapter;
    private long tiendaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_alertas);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Alertas y notificaciones");
        }

        recycler = findViewById(R.id.recyclerAlertas);
        swipe = findViewById(R.id.swipeRefresh);
        progress = findViewById(R.id.progressBar);
        Spinner spinnerFiltro = findViewById(R.id.spinnerFiltro);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlertaAdapter();
        recycler.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("data_login", MODE_PRIVATE);
        tiendaId = prefs.getLong("tienda_id", 1L);

        configurarSpinner(spinnerFiltro);
        cargarAlertas();

        swipe.setOnRefreshListener(this::cargarAlertas);
    }

    private void configurarSpinner(Spinner spinner) {
        String[] opciones = {
                "Todas",
                "Ventas",
                "Nuevos",
                "Editados",
                "Eliminados"
        };

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                opciones
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        adapter.filtrarPorTipo("TODAS");
                        break;
                    case 1:
                        adapter.filtrarPorTipo("VENTA_REALIZADA");
                        break;
                    case 2:
                        adapter.filtrarPorTipo("NUEVO_PRODUCTO");
                        break;
                    case 3:
                        adapter.filtrarPorTipo("PRODUCTO_EDITADO");
                        break;
                    case 4:
                        adapter.filtrarPorTipo("PRODUCTO_ELIMINADO");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void cargarAlertas() {
        progress.setVisibility(View.VISIBLE);
        swipe.setRefreshing(false);

        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        api.obtenerAlertas(tiendaId).enqueue(new Callback<List<Alerta>>() {
            @Override
            public void onResponse(Call<List<Alerta>> call, Response<List<Alerta>> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setLista(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Alerta>> call, Throwable t) {
                progress.setVisibility(View.GONE);
                Toast.makeText(HomeAlertas.this, "Sin conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
