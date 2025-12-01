package com.example.stockbrain.modelo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;

import com.example.stockbrain.logica.InicioSesion;
import com.example.stockbrain.logica.MainActivity;

public class SessionManager {
    private static final String PREF_NAME = "data_login";
    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean estaLogueado() {
        return prefs.getLong("user_id", 0L) != 0L;
    }

    public Long getUserId() {
        return prefs.getLong("user_id", 0L);
    }

    public String getNombre() {
        return prefs.getString("user_nombre", "Usuario");
    }

    public String getEmail() {
        return prefs.getString("user_email", "email@ejemplo.com");
    }

    public String getRol() {
        return prefs.getString("rol", "");
    }

    public Long getTiendaId() {
        return prefs.getLong("tienda_id", 0L);
    }

    public void logout(Context context) {
        prefs.edit().clear().apply();
        context.startActivity(new Intent(context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void guardarNombre(String nombre) {
        prefs.edit().putString("user_nombre", nombre).apply();
    }

    public void guardarEmail(String email) {
        prefs.edit().putString("user_email", email).apply();
    }

    public void guardarUsuarioLogueado(Long id, String nombre, String email, String rol, Long tiendaId) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("user_id", id != null ? id : 0L);
        editor.putString("user_nombre", nombre);
        editor.putString("user_email", email);
        editor.putString("rol", rol);

        if (tiendaId != null) {
            editor.putLong("tienda_id", tiendaId);
            editor.putString("store_id", String.valueOf(tiendaId));
        }

        editor.apply();
    }

    public void guardarTienda(Long tiendaId, String nombreTienda) {
        prefs.edit()
                .putLong("tienda_id", tiendaId)
                .putString("store_id", String.valueOf(tiendaId))
                .putString("nombre_tienda", nombreTienda)
                .apply();
    }
}