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
                    Usuario Usuario = response.body();

                    if (Usuario.getId() == null || Usuario.getEmail() == null) {
                        Toast.makeText(InicioSesion.this, "Datos incompletos del servidor", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SharedPreferences prefs = getSharedPreferences("data_login", MODE_PRIVATE);
                    prefs.edit()
                            .putString("user_id", String.valueOf(Usuario.getId()))
                            .putString("user_email", Usuario.getEmail())
                            .putString("rol", Usuario.getRol())
                            .apply();

                    Log.d(TAG, "Login exitoso: ID=" + Usuario.getId() + ", Rol=" + Usuario.getRol());

                    if ("ADMIN".equalsIgnoreCase(Usuario.getRol())) {
                        startActivity(new Intent(InicioSesion.this, Home.class));
                        finish();
                    } else {
                        startActivity(new Intent(InicioSesion.this, ListaTiendas.class));
                        finish();
                    }

                } else {
                    String error = response.code() == 401 ? "Credenciales inválidas" : "Error del servidor";
                    Toast.makeText(InicioSesion.this, error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Log.e(TAG, "Error de red: " + t.getMessage());
                Toast.makeText(InicioSesion.this, "Sin conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}