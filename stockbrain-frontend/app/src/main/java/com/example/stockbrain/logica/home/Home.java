package com.example.stockbrain.logica.home;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.stockbrain.logica.MainActivity;
import com.example.stockbrain.modelo.SessionManager;

public class Home extends AppCompatActivity {

    private static final String TAG = "Home";

    private TextView txtNombreTienda;
    private ImageButton btnInventario, btnListaProductos, btnVentas;
    private ImageButton btnAlertas, btnSoporte, btnInforme;
    private ImageButton btnInicio, btnAjustes, btnInfo, btnLogout, btnMore;
    private Long userId;
    private String userEmail;
    private static final String EMAIL_SOPORTE = "agalgom316@g.educaand.com";
    private static final String TELEFONO_SOPORTE = "34642330037";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("StockBrain");
        }

        if (!validarSesionAdmin()) {
            return;
        }

        inicializarVistas();

        gestorClicks();

    }

    private boolean validarSesionAdmin() {
        SharedPreferences prefs = getSharedPreferences("data_login", MODE_PRIVATE);
        String rol = prefs.getString("rol", null);
        userId = Long.parseLong(prefs.getString("user_id", "0"));
        userEmail = prefs.getString("user_email", "");

        if (userId == 0 || !"ADMIN".equals(rol)) {
            Toast.makeText(this, "Acceso denegado", Toast.LENGTH_SHORT).show();
            SessionManager.logout(this);
            return false;
        }
        return true;
    }

    private void inicializarVistas() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Gestión de Inventario");
        }

        txtNombreTienda = findViewById(R.id.txtNombreTienda);

        btnInventario = findViewById(R.id.inventario);
        btnListaProductos = findViewById(R.id.btn_lista_productos);
        btnVentas = findViewById(R.id.gestion_de_ventas);
        btnAlertas = findViewById(R.id.alertas_notificaciones);
        btnSoporte = findViewById(R.id.soporte);
        btnInforme = findViewById(R.id.informe);
        btnInicio = findViewById(R.id.inicio);
        btnAjustes = findViewById(R.id.ajustes);
        btnLogout = findViewById(R.id.logout);
        btnMore = findViewById(R.id.more);

        txtNombreTienda = findViewById(R.id.txtNombreTienda);
    }

    private void gestorClicks() {
        btnInventario.setOnClickListener(v -> mostrarInventario());
        btnSoporte.setOnClickListener(v -> mostrarSoporte());
        btnMore.setOnClickListener(this::mostrarPopupMenu);
        btnLogout.setOnClickListener(v -> cerrarSesion());
        btnAjustes.setOnClickListener(v -> mostrarAjustes());
        btnListaProductos.setOnClickListener(v -> mostrarListaProductos());
    }

    private void mostrarAjustes() {
        startActivity(new Intent(Home.this, Configuration.class));
    }

    private void mostrarDialogoLogout() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> SessionManager.logout(this))
                .setNegativeButton("No", null)
                .show();
    }

    private void mostrarInventario(){
        startActivity(new Intent(Home.this, HomeCrearProductos.class));
    }

    private void mostrarListaProductos(){
        startActivity(new Intent(Home.this, HomeListaProductos.class));
    }

    private void mostrarSoporte() {
        String[] opciones = {"Enviar Email", "Contactar (WhatsApp/Teléfono)"};

        new AlertDialog.Builder(this)
                .setTitle("¿Cómo te ayudamos?")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        abrirEmail();
                    } else if (which == 1) {
                        mostrarOpcionesContacto();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void cerrarSesion() {
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Home.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }

    // FUNCIONES SOPORTE
    private void mostrarOpcionesContacto() {
        String[] opciones = {"Llamar al soporte", "WhatsApp", "SMS"};

        new AlertDialog.Builder(this)
                .setTitle("Contactar")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        llamarSoporte();
                    } else if (which == 1) {
                        abrirWhatsApp();
                    } else if (which == 2) {
                        enviarSMS();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void abrirEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL_SOPORTE});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Solicitud de soporte - StockBrain");
        intent.putExtra(Intent.EXTRA_TEXT,
                "Hola equipo,\n\nEstoy teniendo un problema con...\n\n" +
                        "Versión de la app: 1.0\n" +
                        "Dispositivo: " + Build.MODEL + "\n" +
                        "Android: " + Build.VERSION.RELEASE + "\n\nGracias.");

        try {
            startActivity(Intent.createChooser(intent, "Enviar correo con..."));
        } catch (Exception e) {
            mostrarError("No hay apps de correo");
        }
    }

    private void llamarSoporte() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + TELEFONO_SOPORTE));
        startActivity(intent);
    }

    private void abrirWhatsApp() {
        String mensaje = "Hola, necesito soporte con la app de StockBrain.";

        String url = "https://wa.me/" + TELEFONO_SOPORTE + "?text=" + Uri.encode(mensaje);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        intent.setPackage("com.whatsapp");

        try {
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {

                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(Intent.createChooser(webIntent, "Abrir WhatsApp"));
            }
        } catch (Exception e) {
            mostrarError("WhatsApp no está instalado");
        }
    }

    private void enviarSMS() {
        String mensaje = "Hola, necesito ayuda con la app de StockBrain.";

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + TELEFONO_SOPORTE));
        intent.putExtra("sms_body", mensaje);

        try {
            startActivity(Intent.createChooser(intent, "Enviar SMS con..."));
        } catch (Exception e) {
            mostrarError("No se puede enviar SMS");
        }
    }

    private void mostrarError(String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(mensaje)
                .setPositiveButton("OK", null)
                .show();
    }

    // TRES PUNTOS
    private void mostrarPopupMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.menu_redes_more, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.action_github) {
                abrirUrl("https://github.com/AlbertoGalvezGomez");
                return true;
            }
            if (id == R.id.action_twitter) {
                abrirUrl("https://x.com/AlbertoGlv57501");
                return true;
            }
            if (id == R.id.action_feedback) {
                enviarFeedback();
                return true;
            }

            return false;
        });

        popup.show();
    }

    private void abrirUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(Intent.createChooser(intent, "Abrir con..."));
        } catch (Exception e) {
            mostrarError("No se pudo abrir el enlace");
        }
    }

    private void enviarFeedback() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL_SOPORTE});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback - " + getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, "Hola,\n\nTengo una sugerencia:\n\n");

        try {
            startActivity(Intent.createChooser(intent, "Enviar feedback"));
        } catch (Exception e) {
            mostrarError("No hay apps de correo");
        }
    }

}