package com.example.stockbrain.modelo;

import com.google.gson.annotations.SerializedName;

public class VentaRequest {
    @SerializedName("cantidad")
    private int cantidad;

    @SerializedName("fecha")
    private String fecha;

    @SerializedName("producto")
    private Producto producto;

    @SerializedName("tienda")
    private Tienda tienda;

    public VentaRequest() {}

    public VentaRequest(int cantidad, String fecha, Producto producto, Tienda tienda) {
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.producto = producto;
        this.tienda = tienda;
    }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    public Tienda getTienda() { return tienda; }
    public void setTienda(Tienda tienda) { this.tienda = tienda; }
}