package com.example.stockbrain.logica.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stockbrain.R;
import com.example.stockbrain.api.ApiClient;
import com.example.stockbrain.api.ApiService;
import com.example.stockbrain.modelo.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfiguracionUsuario extends AppCompatActivity {

    private EditText editNombre, editEmail, editPassword;
    private Button btnGuardar;
    private Long userId;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil);

        editNombre = findViewById(R.id.editNombreUsuario);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editContrasenya);
        btnGuardar = findViewById(R.id.btnGuardarCambios);

        SharedPreferences prefs = getSharedPreferences("data_login", MODE_PRIVATE);
        userId = Long.parseLong(prefs.getString("user_id", "0"));
        String emailGuardado = prefs.getString("user_email", "");
        editEmail.setText(emailGuardado);

        api = ApiClient.getClient(this).create(ApiService.class);

        btnGuardar.setOnClickListener(v -> actualizarUsuario());
    }

    private void actualizarUsuario() {
        String nuevoNombre = editNombre.getText().toString().trim();
        String nuevoEmail = editEmail.getText().toString().trim();
        String nuevaPassword = editPassword.getText().toString().trim();

        Usuario usuarioActualizado = new Usuario(
                nuevoNombre.isEmpty() ? null : nuevoNombre,
                nuevoEmail.isEmpty() ? null : nuevoEmail,
                nuevaPassword.isEmpty() ? null : nuevaPassword,
                null
        );

        Call<Usuario> call = api.actualizarUsuario(userId, usuarioActualizado);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ConfiguracionUsuario.this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();

                    if (!nuevoEmail.isEmpty()) {
                        SharedPreferences prefs = getSharedPreferences("data_login", MODE_PRIVATE);
                        prefs.edit().putString("user_email", nuevoEmail).apply();
                    }
                } else {
                    Toast.makeText(ConfiguracionUsuario.this, "Error al actualizar: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("UPDATE", "Error HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(ConfiguracionUsuario.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
