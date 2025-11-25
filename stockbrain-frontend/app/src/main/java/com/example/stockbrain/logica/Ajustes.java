package com.example.stockbrain.logica;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.stockbrain.R;
import com.example.stockbrain.api.ApiClient;
import com.example.stockbrain.api.ApiService;
import com.example.stockbrain.logica.home.HomeEditarProducto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ajustes extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvUsername, tvUserEmail;
    private ImageButton btnEditProfile;
    private Button btnChangePassword;
    private Switch switchPush;
    private Button btnFaq, btnSoporte, btnDeleteAccount;
    private ImageButton btnHome, btnSettings, btnLogout, btnMore;
    private boolean isDeleting = false;

    private static final String EMAIL_SOPORTE = "agalgom316@g.educaand.com";
    private static final String TELEFONO_SOPORTE = "34642330037";

    private static final String PREFS_NAME = "settings_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustes);

        initViews();
        setupToolbar();
        setupClickListeners();
        loadUserData();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvUsername = findViewById(R.id.tv_username);
        tvUserEmail = findViewById(R.id.tv_user_email);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnChangePassword = findViewById(R.id.btn_change_password);
        switchPush = findViewById(R.id.switch_push);
        btnFaq = findViewById(R.id.btn_faq);
        btnSoporte = findViewById(R.id.btnSoporte);
        btnDeleteAccount = findViewById(R.id.btn_delete_account);
        btnHome = findViewById(R.id.inicio);
        btnSettings = findViewById(R.id.ajustes);
        btnLogout = findViewById(R.id.logout);
        btnMore = findViewById(R.id.more);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Ajustes");
        }
    }

    private void setupClickListeners() {
        btnFaq.setOnClickListener(v -> startActivity(new Intent(this, Faq.class)));
        btnSoporte.setOnClickListener(v -> mostrarSoporte());
        btnDeleteAccount.setOnClickListener(v -> mostrarDialogoConfirmacionEliminarCuenta());
    }

    private void mostrarDialogoConfirmacionEliminarCuenta() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar cuenta permanentemente")
                .setMessage("¿Estás completamente seguro de que quieres eliminar tu cuenta?\n\n" +
                        "Esta acción es irreversible. Se borrarán todos tus datos, operaciones, " +
                        "favoritos y no podrás recuperarlos nunca.")
                .setPositiveButton("Sí, eliminar mi cuenta", (dialog, which) -> {

                    mostrarDialogoConfirmacionFinal();
                })
                .setNegativeButton("Cancelar", null)
                .setCancelable(true)
                .show();
    }

    private void mostrarDialogoConfirmacionFinal() {
        View view = getLayoutInflater().inflate(R.layout.dialog_confirmar_eliminar, null);
        EditText etConfirmar = view.findViewById(R.id.et_confirmar_texto);

        new AlertDialog.Builder(this)
                .setTitle("Confirma escribiendo ELIMINAR")
                .setView(view)
                .setPositiveButton("Eliminar definitivamente", (dialog, which) -> {
                    String texto = etConfirmar.getText().toString().trim();
                    if ("ELIMINAR".equalsIgnoreCase(texto)) {
                        eliminarCuentaDefinitivamente();
                    } else {
                        Toast.makeText(this, "Debes escribir exactamente 'ELIMINAR' para confirmar", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .setCancelable(true)
                .show();
    }

    private void eliminarCuentaDefinitivamente() {
        if (isDeleting) return;
        isDeleting = true;

        AlertDialog progressDialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_progress)
                .setMessage("Eliminando cuenta permanentemente...")
                .setCancelable(false)
                .create();

        progressDialog.show();

        Long userId = getSharedPreferences("data_login", MODE_PRIVATE)
                .getLong("user_id", 0L);

        if (userId == 0L) {
            progressDialog.dismiss();
            Toast.makeText(this, "Error: usuario no identificado", Toast.LENGTH_LONG).show();
            isDeleting = false;
            return;
        }

        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        api.eliminarUsuario(userId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.dismiss();
                isDeleting = false;

                if (response.isSuccessful()) {
                    Toast.makeText(Ajustes.this, "Cuenta eliminada correctamente", Toast.LENGTH_LONG).show();
                    borrarDatosLocalesYLogout();
                } else {
                    String errorMsg = "Error del servidor (" + response.code() + ")";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception ignored) {

                    }
                    Toast.makeText(Ajustes.this, "No se pudo eliminar la cuenta", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                isDeleting = false;
                Toast.makeText(Ajustes.this, "Error de conexión. Revisa tu internet.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void borrarDatosLocalesYLogout() {
        getSharedPreferences("data_login", MODE_PRIVATE).edit().clear().apply();
        getSharedPreferences("settings_prefs", MODE_PRIVATE).edit().clear().apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finishAffinity();
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("data_login", MODE_PRIVATE);
        String nombre = prefs.getString("user_nombre", "Usuario");
        String email = prefs.getString("user_email", "email@ejemplo.com");

        tvUsername.setText(nombre);
        tvUserEmail.setText(email);
    }

    private void mostrarSoporte() {
        String[] opciones = {"Enviar Email", "WhatsApp / Teléfono"};
        new AlertDialog.Builder(this)
                .setTitle("Soporte")
                .setItems(opciones, (d, w) -> {
                    if (w == 0) abrirEmail();
                    else mostrarOpcionesContacto();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarOpcionesContacto() {
        String[] opciones = {"Llamar", "WhatsApp", "SMS"};
        new AlertDialog.Builder(this)
                .setItems(opciones, (d, w) -> {
                    if (w == 0) llamarSoporte();
                    else if (w == 1) abrirWhatsApp();
                    else enviarSMS();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void abrirEmail() {
        Intent i = new Intent(Intent.ACTION_SEND).setType("message/rfc822")
                .putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL_SOPORTE})
                .putExtra(Intent.EXTRA_SUBJECT, "Soporte StockBrain")
                .putExtra(Intent.EXTRA_TEXT, "Hola,\n\nNecesito ayuda con...\n\nDispositivo: " + Build.MODEL +
                        "\nAndroid: " + Build.VERSION.RELEASE);
        try {
            startActivity(Intent.createChooser(i, "Enviar correo"));
        } catch (Exception e) {
            Toast.makeText(this, "No hay app de correo", Toast.LENGTH_SHORT).show();
        }
    }

    private void llamarSoporte() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + TELEFONO_SOPORTE)));
    }

    private void abrirWhatsApp() {
        String texto = "Hola, necesito soporte con StockBrain";
        String url = "https://wa.me/" + TELEFONO_SOPORTE + "?text=" + Uri.encode(texto);
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "WhatsApp no instalado", Toast.LENGTH_SHORT).show();
        }
    }

    private void enviarSMS() {
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + TELEFONO_SOPORTE));
        i.putExtra("sms_body", "Hola, necesito ayuda con StockBrain");
        try {
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "No se puede enviar SMS", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}