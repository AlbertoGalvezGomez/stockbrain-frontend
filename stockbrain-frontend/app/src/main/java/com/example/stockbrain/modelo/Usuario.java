package com.example.stockbrain.modelo;

import com.google.gson.annotations.SerializedName;

public class Usuario {

    @SerializedName("id")
    private Long id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("email")
    private String email;

    @SerializedName("rol")
    private String rol;

    @SerializedName("tiendaId")
    private Long tiendaId;

    @SerializedName("message")
    private String message;

    // Campo SOLO para registro (no se recibe nunca en login)
    @SerializedName("password")
    private String password;

    // Constructor vacío
    public Usuario() {}

    // Constructor para registro (con password)
    public Usuario(String nombre, String email, String password, String rol) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Long getTiendaId() { return tiendaId; }
    public void setTiendaId(Long tiendaId) { this.tiendaId = tiendaId; }

    public String getMessage() { return message; }

    // Solo para registro
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}