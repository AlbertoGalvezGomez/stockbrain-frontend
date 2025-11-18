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
import com.example.stockbrain.modelo.UsuarioRequest;
import com.example.stockbrain.modelo.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InicioSesion extends AppCompatActivity {

    private static final String TAG = "InicioSesion";
    private EditText editEmail, editPassword;
    private Button btnIniciarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iniciar_sesion);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Iniciar Sesión");
        }

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editContrasenya);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);

        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());
    }

    private void iniciarSesion() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        UsuarioRequest request = new UsuarioRequest(email, password);
        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        Call<Usuario> call = api.login(request);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuario = response.body();

                    SharedPreferences prefs = getSharedPreferences("data_login", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong("user_id", usuario.getId() != null ? usuario.getId() : 0L);
                    editor.putString("user_nombre", usuario.getNombre());
                    editor.putString("user_email", usuario.getEmail());
                    editor.putString("rol", usuario.getRol());
                    if (usuario.getTiendaId() != null) {
                        editor.putLong("tienda_id", usuario.getTiendaId());
                        editor.putString("store_id", String.valueOf(usuario.getTiendaId()));
                    }
                    editor.apply();

                    Log.d(TAG, "Login OK → " + usuario.getNombre() + " (" + usuario.getRol() + ") | Tienda: " + usuario.getTiendaId());

                    Toast.makeText(InicioSesion.this, "¡Bienvenido " + usuario.getNombre() + "!", Toast.LENGTH_SHORT).show();

                    Intent intent = "ADMIN".equalsIgnoreCase(usuario.getRol())
                            ? new Intent(InicioSesion.this, Home.class)
                            : new Intent(InicioSesion.this, ListaTiendas.class);

                    startActivity(intent);
                    finish();

                } else if (response.code() == 401) {
                    Toast.makeText(InicioSesion.this, "Credenciales inválidas", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(InicioSesion.this, "Error del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e(TAG, "Error de conexión: ", t);
                Toast.makeText(InicioSesion.this, "Sin conexión al servidor", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}