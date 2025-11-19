package com.example.stockbrain.logica;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.stockbrain.R;
import com.example.stockbrain.api.ApiClient;
import com.example.stockbrain.api.ApiService;
import com.example.stockbrain.logica.home.Home;
import com.example.stockbrain.modelo.SessionManager;
import com.example.stockbrain.modelo.Tienda;
import com.example.stockbrain.modelo.TiendaRequest;
import com.example.stockbrain.modelo.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearTienda extends AppCompatActivity {

    private static final String TAG = "CrearTienda";

    private EditText editNombreTienda, editUbicacion;
    private Button btnCrearTienda;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_tiendas);

        sessionManager = new SessionManager(this);

        if (!sessionManager.estaLogueado()) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, InicioSesion.class));
            finish();
            return;
        }

        if (!"ADMIN".equalsIgnoreCase(sessionManager.getRol())) {
            Toast.makeText(this, "Solo los administradores pueden crear tiendas", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        inicializarVistas();
        configurarToolbar();
        configurarBotonCrear();
    }

    private void inicializarVistas() {
        editNombreTienda = findViewById(R.id.editNombreTienda);
        editUbicacion = findViewById(R.id.editUbicacion);
        btnCrearTienda = findViewById(R.id.btnCrearTienda);
    }

    private void configurarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Crear Tienda");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void configurarBotonCrear() {
        btnCrearTienda.setOnClickListener(v -> crearTienda());
    }

    private void crearTienda() {
        String nombre = editNombreTienda.getText().toString().trim();
        String ubicacion = editUbicacion.getText().toString().trim();

        if (nombre.isEmpty() || ubicacion.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Long userId = sessionManager.getUserId();

        TiendaRequest request = new TiendaRequest(nombre, ubicacion, userId);

        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        api.crearTienda(request).enqueue(new Callback<Tienda>() {
            @Override
            public void onResponse(Call<Tienda> call, Response<Tienda> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Tienda tienda = response.body();
                    Long tiendaId = tienda.getId();
                    String nombreTienda = tienda.getNombre();

                    Log.d(TAG, "Tienda creada → ID: " + tiendaId + " | " + nombreTienda);

                    sessionManager.guardarTienda(tiendaId, nombreTienda);

                    asignarTiendaAlUsuario(userId, tiendaId);
                } else {
                    Toast.makeText(CrearTienda.this, "Error al crear la tienda", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tienda> call, Throwable t) {
                Log.e(TAG, "Error de red: " + t.getMessage());
                Toast.makeText(CrearTienda.this, "Sin conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void asignarTiendaAlUsuario(Long userId, Long tiendaId) {
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setTiendaId(tiendaId);

        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        api.actualizarUsuario(userId, usuarioActualizado).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Tienda asignada al usuario correctamente");
                    Toast.makeText(CrearTienda.this, "¡Tienda creada y asignada!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CrearTienda.this, "Tienda creada, pero no se pudo asignar", Toast.LENGTH_LONG).show();
                }
                irAlHome();
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(CrearTienda.this, "Tienda creada (sin asignar)", Toast.LENGTH_LONG).show();
                irAlHome();
            }
        });
    }

    private void irAlHome() {
        startActivity(new Intent(CrearTienda.this, Home.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}