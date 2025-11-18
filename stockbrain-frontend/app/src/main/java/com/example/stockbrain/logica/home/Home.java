package com.example.stockbrain.logica.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.stockbrain.R;
import com.example.stockbrain.logica.Configuration;
import com.example.stockbrain.logica.InicioSesion;
import com.example.stockbrain.modelo.SessionManager;

public class Home extends AppCompatActivity {

    private TextView txtNombreTienda;
    private ImageButton btnInventario, btnListaProductos, btnSoporte, btnMore, btnAjustes, btnLogout;

    private SessionManager sessionManager;

    private static final String EMAIL_SOPORTE = "agalgom316@g.educaand.com";
    private static final String TELEFONO_SOPORTE = "34642330037";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        sessionManager = new SessionManager(this);

        if (!sessionManager.estaLogueado()) {
            redirigirALogin();
            return;
        }
        if (!"ADMIN".equalsIgnoreCase(sessionManager.getRol())) {
            Toast.makeText(this, "Solo los administradores pueden acceder", Toast.LENGTH_LONG).show();
            sessionManager.logout(this);
            return;
        }

        inicializarVistas();
        configurarToolbar();
        cargarDatosUsuario();
        configurarClicks();
    }

    private void configurarToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("StockBrain");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    private void cargarDatosUsuario() {
        txtNombreTienda.setText("Bienvenido, " + sessionManager.getNombre());
    }

    private void inicializarVistas() {
        txtNombreTienda = findViewById(R.id.txtNombreTienda);

        btnInventario = findViewById(R.id.inventario);
        btnListaProductos = findViewById(R.id.btn_lista_productos);
        btnSoporte = findViewById(R.id.soporte);
        btnMore = findViewById(R.id.more);
        btnAjustes = findViewById(R.id.ajustes);
        btnLogout = findViewById(R.id.logout);
    }

    private void configurarClicks() {
        btnInventario.setOnClickListener(v -> startActivity(new Intent(this, HomeCrearProductos.class)));
        btnListaProductos.setOnClickListener(v -> startActivity(new Intent(this, HomeListaProductos.class)));
        btnSoporte.setOnClickListener(v -> mostrarSoporte());
        btnAjustes.setOnClickListener(v -> startActivity(new Intent(this, Configuration.class)));
        btnMore.setOnClickListener(this::mostrarPopupMenu);
        btnLogout.setOnClickListener(v -> confirmarLogout());
    }

    private void confirmarLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí", (d, w) -> sessionManager.logout(this))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void redirigirALogin() {
        startActivity(new Intent(this, InicioSesion.class));
        finish();
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

    private void mostrarPopupMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.menu_redes_more, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_github) abrirUrl("https://github.com/AlbertoGalvezGomez");
            else if (id == R.id.action_twitter) abrirUrl("https://x.com/AlbertoGlv57501");
            else if (id == R.id.action_feedback) abrirEmail(); // reutilizamos el mismo
            return true;
        });
        popup.show();
    }

    private void abrirUrl(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(this, "Error al abrir enlace", Toast.LENGTH_SHORT).show();
        }
    }
}