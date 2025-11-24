package com.example.stockbrain.logica;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.stockbrain.R;

public class Ajustes extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvUsername, tvUserEmail;
    private ImageButton btnEditProfile;
    private Button btnChangePassword;
    private Switch switchPush;
    private Button btnFaq, btnContactSupport, btnDeleteAccount;
    private ImageButton btnHome, btnSettings, btnLogout, btnMore;

    private static final String PREFS_NAME = "settings_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);

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
        btnContactSupport = findViewById(R.id.soporte);
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
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("data_login", MODE_PRIVATE);
        String nombre = prefs.getString("user_nombre", "Usuario");
        String email = prefs.getString("user_email", "email@ejemplo.com");

        tvUsername.setText(nombre);
        tvUserEmail.setText(email);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}