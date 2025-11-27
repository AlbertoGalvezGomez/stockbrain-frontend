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

                    sessionManager.guardarTienda(tienda.getId(), tienda.getNombre());

                    Toast.makeText(CrearTienda.this, "¡Tienda creada con éxito!", Toast.LENGTH_LONG).show();
                    irAlHome();

                } else {
                    // Mensajes más claros según el código HTTP
                    String mensaje;
                    switch (response.code()) {
                        case 400:
                            mensaje = "Datos inválidos o ya tienes una tienda";
                            break;
                        case 403:
                            mensaje = "No tienes permiso para crear una tienda";
                            break;
                        case 404:
                            mensaje = "Usuario no encontrado";
                            break;
                        default:
                            mensaje = "Error del servidor: " + response.code();
                            break;
                    }
                    Toast.makeText(CrearTienda.this, mensaje, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Tienda> call, Throwable t) {
                Log.e(TAG, "Error de red", t);
                Toast.makeText(CrearTienda.this, "Sin conexión", Toast.LENGTH_SHORT).show();
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

    // ELIMINA COMPLETAMENTE ESTE MÉTODO → YA NO SE USA NUNCA
    // private void asignarTiendaAlUsuario(...) { ... }
}