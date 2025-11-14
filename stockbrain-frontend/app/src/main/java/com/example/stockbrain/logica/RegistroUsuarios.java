package com.example.stockbrain.logica;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.stockbrain.R;
import com.example.stockbrain.api.ApiClient;
import com.example.stockbrain.api.ApiService;
import com.example.stockbrain.modelo.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroUsuarios extends AppCompatActivity {

    private EditText nombre, email, password;
    private Button btnRegistrar;
    private Spinner rolSpinner;
    private String selectedRole;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrarse);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        nombre = findViewById(R.id.editNombreUsuario);
        email = findViewById(R.id.editEmail);
        password = findViewById(R.id.editContrasenya);
        btnRegistrar = findViewById(R.id.btnRegistrarse);
        rolSpinner = findViewById(R.id.rolSpinner);

        String[] roles = {"Soy vendedor", "Soy cliente"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rolSpinner.setAdapter(adapter);

        rolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRole = roles[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRole = null;
            }
        });

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        String nombreUsuario = nombre.getText().toString().trim();
        String emailUsuario = email.getText().toString().trim();
        String passwordUsuario = password.getText().toString().trim();

        if (nombreUsuario.isEmpty() || emailUsuario.isEmpty() || passwordUsuario.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedRole == null) {
            Toast.makeText(this, "Selecciona un rol", Toast.LENGTH_SHORT).show();
            return;
        }

        String rolSeleccionado = selectedRole.equals("Soy vendedor") ? "ADMIN" : "USER";
        Usuario usuarioNuevo = new Usuario(nombreUsuario, emailUsuario, passwordUsuario, rolSeleccionado);

        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        Call<Usuario> call = api.crearUsuario(usuarioNuevo);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Usuario usuario = response.body();
                    if (usuario != null) {
                        Long idUsuario = usuario.getId();
                        if (idUsuario != null) {
                            SharedPreferences prefs = getSharedPreferences("data_login", MODE_PRIVATE);
                            prefs.edit()
                                    .putString("user_id", String.valueOf(idUsuario))
                                    .putString("rol", rolSeleccionado)
                                    .putString("user_email", emailUsuario)
                                    .apply();

                            Log.d("REGISTRO", "¡ÉXITO! ID guardado: " + idUsuario + " | Rol: " + rolSeleccionado);

                            Toast.makeText(RegistroUsuarios.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();

                            Intent intent = rolSeleccionado.equals("ADMIN")
                                    ? new Intent(RegistroUsuarios.this, CrearTienda.class)
                                    : new Intent(RegistroUsuarios.this, ListaTiendas.class);

                            startActivity(intent);
                            finish();
                        } else {
                            Log.e("REGISTRO", "¡ERROR! Usuario sin ID: " + (usuario.getEmail() != null ? usuario.getEmail() : "desconocido"));
                            Toast.makeText(RegistroUsuarios.this, "Error: El servidor no asignó un ID", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.e("REGISTRO", "¡ERROR! Response body null");
                        Toast.makeText(RegistroUsuarios.this, "Error: Respuesta vacía del servidor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Desconocido";
                        Log.e("REGISTRO", "Error HTTP " + response.code() + ": " + errorBody);
                        Toast.makeText(RegistroUsuarios.this, "Error: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e("REGISTRO", "Error parsing error body: " + e.getMessage());
                        Toast.makeText(RegistroUsuarios.this, "Error del servidor: " + response.code(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(RegistroUsuarios.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}