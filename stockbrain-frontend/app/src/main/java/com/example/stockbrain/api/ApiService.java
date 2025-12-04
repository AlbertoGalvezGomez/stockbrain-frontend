package com.example.stockbrain.api;

import com.example.stockbrain.modelo.Alerta;
import com.example.stockbrain.modelo.DashboardResponse;
import com.example.stockbrain.modelo.UsuarioRequest;
import com.example.stockbrain.modelo.Producto;
import com.example.stockbrain.modelo.TiendaRequest;
import com.example.stockbrain.modelo.Tienda;
import com.example.stockbrain.modelo.Usuario;
import com.example.stockbrain.modelo.UsuarioUpdateRequest;
import com.example.stockbrain.modelo.VentaRequest;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("productos")
    Call<List<Producto>> getProductos();

    @POST("productos")
    Call<Producto> crearProducto(@Body Producto producto);

    @POST("tiendas")
    Call<Tienda> crearTienda(@Body TiendaRequest request);

    @GET("tiendas")
    Call<List<Tienda>> obtenerTiendas(@Query("user_id") Long userId);

    @POST("api/auth/login")
    Call<Usuario> login(@Body UsuarioRequest usuarioRequest);

    @POST("usuarios")
    Call<Usuario> crearUsuario(@Body Usuario usuario);

    @PUT("usuarios/{id}")
    Call<Void> actualizarUsuario(@Path("id") Long id, @Body UsuarioUpdateRequest request);

    @GET("usuarios/{id}")
    Call<Usuario> getUsuario(@Path("id") Long id);

    @DELETE("usuarios/{id}")
    Call<Void> eliminarUsuario(@Path("id") Long id);

    @GET("tiendas/usuario/{userId}")
    Call<Tienda> obtenerTiendaDeUsuario(@Path("userId") Long userId);

    @Multipart
    @POST("productos")
    Call<Producto> crearProductoConImagen(
            @Part("nombre") RequestBody nombre,
            @Part("precio") RequestBody precio,
            @Part("stock") RequestBody stock,
            @Part("descripcion") RequestBody descripcion,
            @Part MultipartBody.Part imagen,
            @Part("tiendaId") RequestBody tiendaId
    );

    @Multipart
    @PUT("productos/{id}")
    Call<Producto> actualizarProducto(
            @Path("id") Long id,
            @Part("nombre") RequestBody nombre,
            @Part("precio") RequestBody precio,
            @Part("stock") RequestBody stock,
            @Part(value = "descripcion") RequestBody descripcion,
            @Part MultipartBody.Part imagen
    );

    @GET("/productos/tienda/{tiendaId}")
    Call<List<Producto>> obtenerProductosPorTienda(@Path("tiendaId") Long tiendaId);

    @DELETE("productos/{id}")
    Call<Void> eliminarProducto(@Path("id") Long id);


    @GET("productos/tienda")
    Call<List<Producto>> getProductosDeMiTienda();

    @POST("ventas")
    Call<Void> crearVenta(@Body VentaRequest ventaRequest);

    @GET("alertas/tienda/{tiendaId}")
    Call<List<Alerta>> obtenerAlertas(@Path("tiendaId") Long tiendaId);

    @GET("api/dashboard/tienda/{tiendaId}")
    Call<DashboardResponse> getDashboard(@Path("tiendaId") Long tiendaId);


}
