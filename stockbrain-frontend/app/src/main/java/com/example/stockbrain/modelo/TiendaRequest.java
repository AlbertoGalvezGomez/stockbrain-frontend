package com.example.stockbrain.modelo;

public class TiendaRequest {
    private String nombre;
    private String ubicacion;
    private Administrador administrador;

    public TiendaRequest(String nombre, String ubicacion, Long adminId) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.administrador = new Administrador(adminId);
    }

    public static class Administrador {
        private Long id;

        public Administrador(Long id) {
            this.id = id;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public Administrador getAdministrador() { return administrador; }
    public void setAdministrador(Administrador administrador) { this.administrador = administrador; }
}