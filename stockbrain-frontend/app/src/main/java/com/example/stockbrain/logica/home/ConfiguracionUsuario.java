package com.example.stockbrain.logica.home;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stockbrain.R;
import com.example.stockbrain.api.ApiClient;
import com.example.stockbrain.api.ApiService;
import com.example.stockbrain.modelo.SessionManager;
import com.example.stockbrain.modelo.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfiguracionUsuario extends AppCompatActivity {

    private EditText editNombre, editEmail, editPassword;
    private Button btnGuardar;
    private SessionManager sessionManager;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil);

        sessionManager = new SessionManager(this);
        api = ApiClient.getClient(this).create(ApiService.class);

        inicializarVistas();
        cargarDatosActuales();
        configurarBotonGuardar();
    }

    private void inicializarVistas() {
        editNombre = findViewById(R.id.editNombreUsuario);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editContrasenya);
        btnGuardar = findViewById(R.id.btnGuardarCambios);
    }

    private void cargarDatosActuales() {
        editNombre.setText(sessionManager.getNombre());
        editEmail.setText(sessionManager.getEmail());
        editPassword.setHint("Dejar en blanco si no quieres cambiarla");
    }

    private void configurarBotonGuardar() {
        btnGuardar.setOnClickListener(v -> actualizarPerfil());
    }

    private void actualizarPerfil() {
        String nuevoNombre = editNombre.getText().toString().trim();
        String nuevoEmail = editEmail.getText().toString().trim();
        String nuevaPassword = editPassword.getText().toString().trim();

        if (nuevoNombre.isEmpty() && nuevoEmail.isEmpty() && nuevaPassword.isEmpty()) {
            Toast.makeText(this, "No has cambiado nada", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nuevoNombre.isEmpty()) {
            Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setNombre(nuevoNombre.isEmpty() ? null : nuevoNombre);
        usuarioActualizado.setEmail(nuevoEmail.isEmpty() ? null : nuevoEmail);
        if (!nuevaPassword.isEmpty()) {

        }

        Long userId = sessionManager.getUserId();

        api.actualizarUsuario(userId, usuarioActualizado).enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!nuevoNombre.isEmpty()) {
                        sessionManager.guardarNombre(nuevoNombre);
                    }
                    if (!nuevoEmail.isEmpty()) {
                        sessionManager.guardarEmail(nuevoEmail);
                    }

                    Toast.makeText(ConfiguracionUsuario.this, "Perfil actualizado correctamente", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(ConfiguracionUsuario.this, "Error al guardar cambios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(ConfiguracionUsuario.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}