package com.example.stockbrain.modelo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AlertDialog;
import com.example.stockbrain.logica.InicioSesion;

public class SessionManager {

    private static final String PREF_NAME = "data_login";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_ROL = "rol";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // === CERRAR SESIÓN ===
    public static void logout(Context context) {
        logout(context, false);
    }

    public static void logout(Context context, boolean showDialog) {
        if (showDialog) {
            new AlertDialog.Builder(context)
                    .setTitle("Cerrar Sesión")
                    .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                    .setPositiveButton("Sí", (dialog, which) -> performLogout(context))
                    .setNegativeButton("No", null)
                    .show();
        } else {
            performLogout(context);
        }
    }

    private static void performLogout(Context context) {
        getPrefs(context).edit().clear().apply();

        Intent intent = new Intent(context, InicioSesion.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        if (context instanceof android.app.Activity) {
            ((android.app.Activity) context).finishAffinity(); // Cierra todas
        }
    }

    // === VERIFICAR SESIÓN ===
    public static boolean isLoggedIn(Context context) {
        return getUserId(context) != null;
    }

    public static boolean isAdmin(Context context) {
        return "ADMIN".equals(getRol(context));
    }

    public static boolean isUser(Context context) {
        return "USER".equals(getRol(context));
    }

    // === OBTENER DATOS ===
    public static Long getUserId(Context context) {
        String id = getPrefs(context).getString(KEY_USER_ID, null);
        return id != null ? Long.parseLong(id) : null;
    }

    public static String getUserEmail(Context context) {
        return getPrefs(context).getString(KEY_USER_EMAIL, "");
    }

    public static String getRol(Context context) {
        return getPrefs(context).getString(KEY_ROL, null);
    }

}