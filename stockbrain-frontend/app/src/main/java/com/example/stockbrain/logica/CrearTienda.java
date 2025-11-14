package com.example.stockbrain.logica;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.stockbrain.modelo.TiendaRequest;
import com.example.stockbrain.modelo.Tienda;
import com.example.stockbrain.modelo.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearTienda extends AppCompatActivity {
    private static final String TAG = "CrearTienda";
    private EditText editNombreTienda, editUbicacion;
    private Button btnCrearTienda;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_tiendas);

        editNombreTienda = findViewById(R.id.editNombreTienda);
        editUbicacion = findViewById(R.id.editUbicacion);
        btnCrearTienda = findViewById(R.id.btnCrearTienda);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Crear Tienda");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = getSharedPreferences("data_login", MODE_PRIVATE);
        String userIdStr = prefs.getString("user_id", null);
        Log.d(TAG, "User ID: " + userIdStr);

        if (userIdStr == null) {
            Toast.makeText(this, "Error: Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, RegistroUsuarios.class));
            finish();
            return;
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error: ID de usuario inválido: " + userIdStr);
            Toast.makeText(this, "Error: ID de usuario inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnCrearTienda.setOnClickListener(v -> {
            String nombre = editNombreTienda.getText().toString().trim();
            String ubicacion = editUbicacion.getText().toString().trim();

            if (nombre.isEmpty() || ubicacion.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            TiendaRequest request = new TiendaRequest(nombre, ubicacion, userId);
            Log.d(TAG, "Enviando: nombre=" + nombre + ", ubicacion=" + ubicacion + ", admin_id=" + userId);

            ApiService api = ApiClient.getClient(this).create(ApiService.class);
            Call<Tienda> call = api.crearTienda(request);

            call.enqueue(new Callback<Tienda>() {
                @Override
                public void onResponse(Call<Tienda> call, Response<Tienda> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Tienda tienda = response.body();
                        Long tiendaId = tienda.getId();
                        String nombreTienda = tienda.getNombre();

                        Log.d(TAG, "Tienda creada → ID: " + tiendaId + ", Nombre: " + nombreTienda);

                        prefs.edit()
                                .putString("store_id", String.valueOf(tiendaId))
                                .putString("tienda_id", String.valueOf(tiendaId))
                                .putString("nombre_tienda", nombreTienda)
                                .apply();

                        // Actualiza usuario
                        updateUserTiendaId(userId, tiendaId);
                    } else {
                        Log.e(TAG, "Error al crear tienda, código: " + response.code());
                        Toast.makeText(CrearTienda.this, "Error al crear tienda", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Tienda> call, Throwable t) {
                    Log.e(TAG, "Fallo al crear tienda: " + t.getMessage());
                    Toast.makeText(CrearTienda.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateUserTiendaId(Long userId, Long tiendaId) {
        Usuario updatedUser = new Usuario();
        updatedUser.setTiendaId(tiendaId);

        ApiService userApi = ApiClient.getClient(this).create(ApiService.class);
        Call<Usuario> updateCall = userApi.actualizarUsuario(userId, updatedUser);

        updateCall.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Usuario actualizado con tienda_id: " + tiendaId);
                    Toast.makeText(CrearTienda.this, "¡Tienda creada y asignada!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Error al asignar tienda al usuario");
                    Toast.makeText(CrearTienda.this, "Tienda creada, pero no asignada", Toast.LENGTH_LONG).show();
                }
                goToHome();
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e(TAG, "Error de red al asignar tienda: " + t.getMessage());
                Toast.makeText(CrearTienda.this, "Tienda creada (sin asignar)", Toast.LENGTH_LONG).show();
                goToHome();
            }
        });
    }

    private void goToHome() {
        Intent intent = new Intent(CrearTienda.this, Home.class);
        startActivity(intent);
        finish();
    }
}