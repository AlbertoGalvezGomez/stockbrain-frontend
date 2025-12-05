package com.example.stockbrain.logica;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.stockbrain.R;
import com.example.stockbrain.api.ApiClient;
import com.example.stockbrain.api.ApiService;
import com.example.stockbrain.modelo.UsuarioUpdateRequest;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarPerfil extends AppCompatActivity {

    private TextInputEditText editNombreUsuario, editContrasenya;
    private Button btnGuardarCambios;
    private long userId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Editar Perfil");
        }

        editNombreUsuario = findViewById(R.id.editNombreUsuario);
        editContrasenya = findViewById(R.id.editContrasenya);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        cargarDatosActuales();

        btnGuardarCambios.setOnClickListener(v -> validarYGuardar());
    }

    private void cargarDatosActuales() {
        SharedPreferences prefs = getSharedPreferences("data_login", MODE_PRIVATE);
        String nombre = prefs.getString("user_nombre", "Usuario");
        userId = prefs.getLong("user_id", 0L);

        editNombreUsuario.setText(nombre);
    }

    private void validarYGuardar() {
        String nuevoNombre = editNombreUsuario.getText().toString().trim();
        String nuevaPass = editContrasenya.getText().toString();

        if (TextUtils.isEmpty(nuevoNombre)) {
            editNombreUsuario.setError("El nombre es obligatorio");
            editNombreUsuario.requestFocus();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Guardando cambios...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        UsuarioUpdateRequest request = new UsuarioUpdateRequest();
        request.setNombre(nuevoNombre);

        if (!TextUtils.isEmpty(nuevaPass)) {
            request.setPassword(nuevaPass);
        }

        api.actualizarUsuario(userId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    SharedPreferences.Editor editor = getSharedPreferences("data_login", MODE_PRIVATE).edit();
                    editor.putString("user_nombre", nuevoNombre);
                    editor.apply();

                    Toast.makeText(EditarPerfil.this, "¡Perfil actualizado!", Toast.LENGTH_LONG).show();

                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditarPerfil.this, "Error del servidor. Inténtalo más tarde.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(EditarPerfil.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}