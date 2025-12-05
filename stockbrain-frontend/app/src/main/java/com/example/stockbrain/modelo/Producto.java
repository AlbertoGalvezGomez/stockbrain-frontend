package com.example.stockbrain.modelo;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

import kotlin.jvm.Transient;

public class Producto implements Parcelable {

    private Long id;
    private String nombre;
    private double precio;
    private int stock;
    private String descripcion;

    @SerializedName("imagenRuta")
    private String imagenRuta;

    @SerializedName("imagenUrl")
    private String imagenUrl;

    public Producto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getImagenRuta() { return imagenRuta; }
    public void setImagenRuta(String imagenRuta) { this.imagenRuta = imagenRuta; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }


    @Transient
    private int cantidadVendidaTemp = 0;

    public int getCantidadVendidaTemp() { return cantidadVendidaTemp; }
    public void setCantidadVendidaTemp(int cantidadVendidaTemp) { this.cantidadVendidaTemp = cantidadVendidaTemp; }

    protected Producto(Parcel in) {
        id = in.readLong();
        if (id == 0) id = null;
        nombre = in.readString();
        precio = in.readDouble();
        stock = in.readInt();
        descripcion = in.readString();
        imagenRuta = in.readString();
        imagenUrl = in.readString();
        cantidadVendidaTemp = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id != null ? id : 0);
        dest.writeString(nombre);
        dest.writeDouble(precio);
        dest.writeInt(stock);
        dest.writeString(descripcion);
        dest.writeString(imagenRuta);
        dest.writeString(imagenUrl);
        dest.writeInt(cantidadVendidaTemp);
    }

    public static final Creator<Producto> CREATOR = new Creator<Producto>() {
        @Override
        public Producto createFromParcel(Parcel in) { return new Producto(in); }

        @Override
        public Producto[] newArray(int size) { return new Producto[size]; }
    };

    @Override
    public int describeContents() { return 0; }
}