package com.example.stockbrain.modelo;

import java.util.List;

public class DashboardResponse {
    private Double ventasHoy;
    private Long totalProductos;
    private Long stockBajo;
    private List<VentaDia> ventas7Dias;
    private List<ProductoVendido> top5Productos;

    public DashboardResponse() {}

    public Double getVentasHoy() { return ventasHoy; }
    public Long getTotalProductos() { return totalProductos; }
    public Long getStockBajo() { return stockBajo; }
    public List<VentaDia> getVentas7Dias() { return ventas7Dias; }
    public List<ProductoVendido> getTop5Productos() { return top5Productos; }
}