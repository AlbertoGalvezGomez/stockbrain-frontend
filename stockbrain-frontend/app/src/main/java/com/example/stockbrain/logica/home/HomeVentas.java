package com.example.stockbrain.logica.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.stockbrain.R;
import com.example.stockbrain.api.ApiClient;
import com.example.stockbrain.api.ApiService;
import com.example.stockbrain.modelo.Producto;
import com.example.stockbrain.modelo.Tienda;
import com.example.stockbrain.modelo.VentaRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeVentas extends AppCompatActivity {

    private Producto productoSeleccionado;
    private Tienda tiendaActual;

    private TextInputEditText etCantidad;
    private MaterialButton btnVender;
    private FloatingActionButton fabBuscarProducto;

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    productoSeleccionado = result.getData().getParcelableExtra("producto");
                    actualizarVistaProducto();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_ventas);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Ventas");
        }

        SharedPreferences prefs = getSharedPreferences("data_login", MODE_PRIVATE);
        long tiendaId = prefs.getLong("tienda_id", 0L);
        if (tiendaId == 0L) {
            Toast.makeText(this, "No tienes tienda asignada", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        tiendaActual = new Tienda();
        tiendaActual.setId(tiendaId);

        etCantidad = findViewById(R.id.etCantidad);
        btnVender = findViewById(R.id.btnVender);
        fabBuscarProducto = findViewById(R.id.fabBuscarProducto);

        fabBuscarProducto.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeProductosVender.class);
            launcher.launch(intent);
        });

        btnVender.setOnClickListener(v -> realizarVenta());
    }

    private void actualizarVistaProducto() {
        if (productoSeleccionado != null) {
            ((TextView) findViewById(R.id.tvProductoNombre)).setText(productoSeleccionado.getNombre());
            ((TextView) findViewById(R.id.tvStockActual)).setText("Stock actual: " + productoSeleccionado.getStock());
        } else {
            ((TextView) findViewById(R.id.tvProductoNombre)).setText("Selecciona un producto");
            ((TextView) findViewById(R.id.tvStockActual)).setText("Stock: -");
        }
    }

    private void realizarVenta() {
        if (productoSeleccionado == null) {
            Toast.makeText(this, "Selecciona un producto", Toast.LENGTH_SHORT).show();
            return;
        }

        String cantStr = etCantidad.getText().toString().trim();
        if (cantStr.isEmpty()) {
            Toast.makeText(this, "Ingresa cantidad", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidad = Integer.parseInt(cantStr);
        if (cantidad <= 0) {
            Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cantidad > productoSeleccionado.getStock()) {
            Toast.makeText(this, "Stock insuficiente", Toast.LENGTH_LONG).show();
            return;
        }

        VentaRequest venta = new VentaRequest(cantidad, null, productoSeleccionado, tiendaActual);

        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        api.crearVenta(venta).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(HomeVentas.this, "¡Venta realizada!", Toast.LENGTH_LONG).show();
                    productoSeleccionado.setStock(productoSeleccionado.getStock() - cantidad);
                    actualizarVistaProducto();
                    etCantidad.setText("");
                } else if (response.code() == 400) {
                    Toast.makeText(HomeVentas.this, "Stock insuficiente en servidor", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(HomeVentas.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(HomeVentas.this, "Sin conexión", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}