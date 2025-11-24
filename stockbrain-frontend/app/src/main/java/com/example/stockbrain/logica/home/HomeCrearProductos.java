package com.example.stockbrain.logica.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.example.stockbrain.R;
import com.example.stockbrain.api.ApiClient;
import com.example.stockbrain.api.ApiService;
import com.example.stockbrain.logica.InicioSesion;
import com.example.stockbrain.modelo.Producto;
import com.example.stockbrain.modelo.SessionManager;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.*;

public class HomeCrearProductos extends AppCompatActivity {

    private static final String TAG = "HomeCrearProductos";

    private LinearLayout linearLayout1, linearLayout2;
    private ImageView imgPreview;
    private EditText editNombre, editStock, editPrecio, editDescripcion;
    private Button btnAgregar, btnSeleccionar, btnGuardar;
    private Uri imagenUri;
    private SessionManager sessionManager;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) abrirGaleria();
                else Toast.makeText(this, "Permiso denegado para leer imágenes", Toast.LENGTH_LONG).show();
            });

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imagenUri = result.getData().getData();
                    imgPreview.setImageURI(imagenUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_inventario);

        sessionManager = new SessionManager(this);

        if (!sessionManager.estaLogueado()) {
            startActivity(new Intent(this, InicioSesion.class));
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Crear producto");
        }

        inicializarVistas();
        configurarClicks();
    }

    private void inicializarVistas() {
        linearLayout1 = findViewById(R.id.linearLayout1);
        linearLayout2 = findViewById(R.id.linearLayout2);
        imgPreview = findViewById(R.id.imgPreview);
        btnAgregar = findViewById(R.id.btnAgregar);
        btnSeleccionar = findViewById(R.id.btnSeleccionar);
        btnGuardar = findViewById(R.id.btnGuardar);

        editNombre = findViewById(R.id.editNombre);
        editStock = findViewById(R.id.editStock);
        editPrecio = findViewById(R.id.editPrecio);
        editDescripcion = findViewById(R.id.editDescripcion);
    }

    private void configurarClicks() {
        btnAgregar.setOnClickListener(v -> activarLayout2());
        btnSeleccionar.setOnClickListener(v -> seleccionarImagen());
        btnGuardar.setOnClickListener(v -> guardarProducto());
    }

    private void activarLayout2() {
        linearLayout1.setVisibility(View.GONE);
        linearLayout2.setVisibility(View.VISIBLE);
    }

    private void seleccionarImagen() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            abrirGaleria();
        } else {
            requestPermissionLauncher.launch(permission);
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void guardarProducto() {
        String nombre = editNombre.getText().toString().trim();
        String stockStr = editStock.getText().toString().trim();
        String precioStr = editPrecio.getText().toString().trim();

        if (nombre.isEmpty() || stockStr.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(this, "Nombre, stock y precio son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        int stock;
        double precio;
        try {
            stock = Integer.parseInt(stockStr);
            precio = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Stock y precio deben ser números válidos", Toast.LENGTH_SHORT).show();
            return;
        }

        Long tiendaId = sessionManager.getTiendaId();
        if (tiendaId == null || tiendaId == 0L) {
            Toast.makeText(this, "Error: no tienes tienda asignada", Toast.LENGTH_LONG).show();
            return;
        }

        MultipartBody.Part imagenPart = null;
        if (imagenUri != null) {
            File file = comprimirImagen(imagenUri);
            if (file != null && file.exists()) {
                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
                imagenPart = MultipartBody.Part.createFormData("imagen", file.getName(), fileBody);
            }
        }

        RequestBody nombrePart = RequestBody.create(MediaType.parse("text/plain"), nombre);
        RequestBody precioPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(precio));
        RequestBody stockPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(stock));
        RequestBody descripcionPart = RequestBody.create(MediaType.parse("text/plain"), editDescripcion.getText().toString().trim());
        RequestBody tiendaIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(tiendaId));

        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        api.crearProductoConImagen(nombrePart, precioPart, stockPart, descripcionPart, imagenPart, tiendaIdPart)
                .enqueue(new Callback<Producto>() {
                    @Override
                    public void onResponse(Call<Producto> call, Response<Producto> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(HomeCrearProductos.this, "¡Producto creado!", Toast.LENGTH_LONG).show();
                            limpiarFormulario();
                            linearLayout1.setVisibility(View.VISIBLE);
                            linearLayout2.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(HomeCrearProductos.this, "Error al crear producto", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Producto> call, Throwable t) {
                        Toast.makeText(HomeCrearProductos.this, "Error de conexión", Toast.LENGTH_SHORT).show();
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

    private void limpiarFormulario() {
        editNombre.setText("");
        editStock.setText("");
        editPrecio.setText("");
        editDescripcion.setText("");
        imgPreview.setImageResource(R.drawable.hide_image);
        imagenUri = null;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}