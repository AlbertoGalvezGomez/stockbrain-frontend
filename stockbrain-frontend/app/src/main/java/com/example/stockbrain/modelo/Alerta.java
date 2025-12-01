package com.example.stockbrain.modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class Alerta implements Parcelable {
    private Long id;
    private String tipo;
    private String mensaje;
    private String fecha;
    private Producto producto;

    public Alerta() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    protected Alerta(Parcel in) {
        id = in.readLong();
        tipo = in.readString();
        mensaje = in.readString();
        fecha = in.readString();
        producto = in.readParcelable(Producto.class.getClassLoader());
    }

    public static final Creator<Alerta> CREATOR = new Creator<Alerta>() {
        @Override public Alerta createFromParcel(Parcel in) { return new Alerta(in); }
        @Override public Alerta[] newArray(int size) { return new Alerta[size]; }
    };

    @Override public int describeContents() { return 0; }
    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(tipo);
        dest.writeString(mensaje);
        dest.writeString(fecha);
        dest.writeParcelable(producto, flags);
    }
}