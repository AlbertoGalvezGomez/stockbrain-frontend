package com.example.stockbrain.logica;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

public class ListaTiendasUsuarios extends AppCompatActivity {

    private static final String TAG = "ListaTiendas";

    private RecyclerView recyclerViewTiendas;
    private TiendaAdapter tiendaAdapter;
    private SessionManager sessionManager;
    private ImageButton btnAjustes, btnLogout, btnMore;
    private static final String EMAIL_SOPORTE = "agalgom316@g.educaand.com";
    private static final String TELEFONO_SOPORTE = "34642330037";

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
        inicializarVistas();
        configurarClicks();
    }

    public void inicializarVistas() {
        btnAjustes = findViewById(R.id.ajustes);
        btnLogout = findViewById(R.id.logout);
        btnMore = findViewById(R.id.more);
    }

    public void configurarClicks() {
        btnAjustes.setOnClickListener(v -> startActivity(new Intent(this, Ajustes.class)));
        btnLogout.setOnClickListener(v -> confirmarLogout());
        btnMore.setOnClickListener(this::mostrarPopupMenu);
    }

    private void confirmarLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí", (d, w) -> sessionManager.logout(this))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarPopupMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.menu_redes_more, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_github) abrirUrl("https://github.com/AlbertoGalvezGomez");
            else if (id == R.id.action_twitter) abrirUrl("https://x.com/AlbertoGlv57501");
            else if (id == R.id.action_compartir) abrirEmail();
            return true;
        });
        popup.show();
    }

    private void abrirUrl(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(this, "Error al abrir enlace", Toast.LENGTH_SHORT).show();
        }
    }

    private void abrirEmail() {
        Intent i = new Intent(Intent.ACTION_SEND).setType("message/rfc822")
                .putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL_SOPORTE})
                .putExtra(Intent.EXTRA_SUBJECT, "Soporte StockBrain")
                .putExtra(Intent.EXTRA_TEXT, "Hola,\n\nNecesito ayuda con...\n\nDispositivo: " + Build.MODEL +
                        "\nAndroid: " + Build.VERSION.RELEASE);
        try {
            startActivity(Intent.createChooser(i, "Enviar correo"));
        } catch (Exception e) {
            Toast.makeText(this, "No hay app de correo", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarTiendas(Long userId) {
        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        api.obtenerTiendas(userId).enqueue(new Callback<List<Tienda>>() {
            @Override
            public void onResponse(Call<List<Tienda>> call, Response<List<Tienda>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tiendaAdapter.setTiendas(response.body());
                } else {
                    Toast.makeText(ListaTiendasUsuarios.this, "No hay tiendas disponibles", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Tienda>> call, Throwable t) {
                Toast.makeText(ListaTiendasUsuarios.this, "Sin conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}