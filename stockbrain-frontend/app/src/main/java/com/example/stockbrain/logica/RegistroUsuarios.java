package com.example.stockbrain.logica;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.stockbrain.modelo.SessionManager;
import com.example.stockbrain.modelo.Usuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroUsuarios extends AppCompatActivity {

    private EditText nombre, email, password;
    private Button btnRegistrar;
    private Spinner rolSpinner;
    private String selectedRole;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrarse);

        sessionManager = new SessionManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Crear cuenta");
        }

        inicializarVistas();
        configurarSpinner();
        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void inicializarVistas() {
        nombre = findViewById(R.id.editNombreUsuario);
        email = findViewById(R.id.editEmail);
        password = findViewById(R.id.editContrasenya);
        btnRegistrar = findViewById(R.id.btnRegistrarse);
        rolSpinner = findViewById(R.id.rolSpinner);
    }

    private void configurarSpinner() {
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
    }

    private void registrarUsuario() {
        String nombreUsuario = nombre.getText().toString().trim();
        String emailUsuario = email.getText().toString().trim();
        String passwordUsuario = password.getText().toString().trim();

        if (nombreUsuario.isEmpty() || emailUsuario.isEmpty() || passwordUsuario.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passwordUsuario.length() < 4) {
            Toast.makeText(this, "La contraseña debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedRole == null) {
            Toast.makeText(this, "Selecciona si eres vendedor o cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        String rolBackend = selectedRole.equals("Soy vendedor") ? "ADMIN" : "USER";

        Usuario usuarioNuevo = new Usuario();
        usuarioNuevo.setNombre(nombreUsuario);
        usuarioNuevo.setEmail(emailUsuario);
        usuarioNuevo.setPassword(passwordUsuario);
        usuarioNuevo.setRol(rolBackend);

        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        Call<Usuario> call = api.crearUsuario(usuarioNuevo);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuarioCreado = response.body();

                    sessionManager.guardarUsuarioLogueado(
                            usuarioCreado.getId(),
                            usuarioCreado.getNombre(),
                            usuarioCreado.getEmail(),
                            rolBackend,
                            null
                    );

                    Toast.makeText(RegistroUsuarios.this, "¡Cuenta creada con éxito!", Toast.LENGTH_LONG).show();

                    Intent intent = rolBackend.equals("ADMIN")
                            ? new Intent(RegistroUsuarios.this, CrearTienda.class)
                            : new Intent(RegistroUsuarios.this, ListaTiendasUsuarios.class);

                    startActivity(intent);
                    finish();

                } else {
                    if (response.code() == 400) {
                        Toast.makeText(RegistroUsuarios.this, "El formato del correo electrónico no es válido", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RegistroUsuarios.this, "Este correo ya está registrado o el formato es incorrecto", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(RegistroUsuarios.this, "Sin conexión", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}