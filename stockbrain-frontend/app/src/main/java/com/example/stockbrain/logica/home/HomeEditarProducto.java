package com.example.stockbrain.logica.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.stockbrain.R;
import com.example.stockbrain.api.ApiClient;
import com.example.stockbrain.api.ApiService;
import com.example.stockbrain.logica.InicioSesion;
import com.example.stockbrain.modelo.Producto;
import com.example.stockbrain.modelo.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeEditarProducto extends AppCompatActivity {

    private static final int REQUEST_GALERIA = 100;
    private static final String TAG = "HomeEditarProducto";

    private ImageView imgPreview;
    private EditText editNombre, editStock, editPrecio, editDescripcion;
    private Button btnActualizar, btnSeleccionar, btnEliminar;
    private Uri imagenUri;
    private SessionManager sessionManager;

    private Producto productoActual;
    private Long productoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actualizar_eliminar_producto);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Editar producto");
        }

        sessionManager = new SessionManager(this);
        if (!sessionManager.estaLogueado()) {
            startActivity(new Intent(this, InicioSesion.class));
            finish();
            return;
        }

        productoActual = getIntent().getParcelableExtra("producto");
        if (productoActual == null || productoActual.getId() == null) {
            Toast.makeText(this, "Error: producto no válido", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        productoId = productoActual.getId();

        inicializarVistas();
        cargarDatosProducto();
        configurarClicks();
    }

    private void inicializarVistas() {
        imgPreview = findViewById(R.id.imgPreview);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnSeleccionar = findViewById(R.id.btnSeleccionar);
        btnEliminar = findViewById(R.id.btnEliminar);
        editNombre = findViewById(R.id.editNombre);
        editStock = findViewById(R.id.editStock);
        editPrecio = findViewById(R.id.editPrecio);
        editDescripcion = findViewById(R.id.editDescripcion);
    }

    private void cargarDatosProducto() {
        editNombre.setText(productoActual.getNombre());
        editPrecio.setText(String.valueOf(productoActual.getPrecio()));
        editStock.setText(String.valueOf(productoActual.getStock()));
        editDescripcion.setText(productoActual.getDescripcion() != null ? productoActual.getDescripcion() : "");

        String url = productoActual.getImagenUrl();
        if (url != null && !url.isEmpty()) {
            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.hide_image)
                    .error(R.drawable.hide_image)
                    .into(imgPreview);
        } else {
            imgPreview.setImageResource(R.drawable.hide_image);
        }
    }

    private void configurarClicks() {
        btnSeleccionar.setOnClickListener(v -> abrirGaleria());
        btnActualizar.setOnClickListener(v -> actualizarProducto());
        btnEliminar.setOnClickListener(v -> mostrarDialogoEliminar());
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALERIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALERIA && resultCode == RESULT_OK && data != null) {
            imagenUri = data.getData();
            imgPreview.setImageURI(imagenUri);
        }
    }

    private void actualizarProducto() {
        String nombre = editNombre.getText().toString().trim();
        String precioStr = editPrecio.getText().toString().trim();
        String stockStr = editStock.getText().toString().trim();

        if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
            Toast.makeText(this, "Nombre, precio y stock obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio = Double.parseDouble(precioStr);
        int stock = Integer.parseInt(stockStr);

        RequestBody nombrePart = RequestBody.create(MediaType.parse("text/plain"), nombre);
        RequestBody precioPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(precio));
        RequestBody stockPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(stock));
        RequestBody descripcionPart = RequestBody.create(MediaType.parse("text/plain"),
                editDescripcion.getText().toString().trim());

        MultipartBody.Part imagenPart = null;
        if (imagenUri != null) {
            File file = comprimirImagen(imagenUri);
            if (file != null && file.exists()) {
                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
                imagenPart = MultipartBody.Part.createFormData("imagen", file.getName(), fileBody);
            }
        }

        ApiService api = ApiClient.getClient(this).create(ApiService.class);

        api.actualizarProducto(
                productoId,
                nombrePart,
                precioPart,
                stockPart,
                descripcionPart,
                imagenPart
        ).enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(HomeEditarProducto.this, "Producto actualizado correctamente", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(HomeEditarProducto.this, "Error del servidor: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                Toast.makeText(HomeEditarProducto.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoEliminar() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("¿Estás seguro de que quieres eliminar \"" + productoActual.getNombre() + "\"?\n\nEsta acción no se puede deshacer.")
                .setPositiveButton("Sí, eliminar", (dialog, which) -> eliminarProducto())
                .setNegativeButton("Cancelar", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void eliminarProducto() {
        ApiService api = ApiClient.getClient(this).create(ApiService.class);

        api.eliminarProducto(productoId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(HomeEditarProducto.this, "Producto eliminado correctamente", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);  // para que la lista se refresque
                    finish();
                } else {
                    Toast.makeText(HomeEditarProducto.this, "Error al eliminar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(HomeEditarProducto.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File comprimirImagen(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            File file = new File(getCacheDir(), "producto_" + System.currentTimeMillis() + ".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

            if (baos.size() > 2 * 1024 * 1024) {
                baos.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.close();
            return file;
        } catch (Exception e) {
            Log.e(TAG, "Error comprimiendo imagen", e);
            return null;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}



