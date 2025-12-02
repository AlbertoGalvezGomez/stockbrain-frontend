package com.example.stockbrain.modelo;

import com.google.gson.annotations.SerializedName;

public class TiendaRequest {
    private String nombre;
    private String ubicacion;

    @SerializedName("administradorId")
    private Long administradorId;

    public TiendaRequest(String nombre, String ubicacion, Long adminId) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.administradorId = adminId;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public Long getAdministradorId() { return administradorId; }
    public void setAdministradorId(Long administradorId) { this.administradorId = administradorId; }
}