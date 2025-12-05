package com.example.stockbrain.logica.home;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stockbrain.R;
import com.example.stockbrain.api.ApiClient;
import com.example.stockbrain.api.ApiService;
import com.example.stockbrain.modelo.DashboardResponse;
import com.example.stockbrain.modelo.ProductoVendido;
import com.example.stockbrain.modelo.VentaDia;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeDashboard extends AppCompatActivity {

    private TextView tvVentasHoy, tvProductosTotal, tvStockBajo, tvError;
    private BarChart barChart;
    private LinearLayout layoutContent, layoutTop5;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        tvVentasHoy = findViewById(R.id.tvVentasHoy);
        tvProductosTotal = findViewById(R.id.tvProductosTotal);
        tvStockBajo = findViewById(R.id.tvStockBajo);
        tvError = findViewById(R.id.tvError);
        barChart = findViewById(R.id.barChart);
        layoutContent = findViewById(R.id.layoutContent);
        layoutTop5 = findViewById(R.id.layoutTop5);
        progressBar = findViewById(R.id.progressBar);

        cargarDatos();
    }

    private void cargarDatos() {
        long tiendaId = getSharedPreferences("data_login", MODE_PRIVATE).getLong("tienda_id", 1L);

        progressBar.setVisibility(View.VISIBLE);
        layoutContent.setVisibility(View.GONE);
        tvError.setVisibility(View.GONE);

        ApiService api = ApiClient.getClient(this).create(ApiService.class);
        api.getDashboard(tiendaId).enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    mostrarDatos(response.body());
                } else {
                    tvError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void mostrarDatos(DashboardResponse data) {

        NumberFormat euroFormat = NumberFormat.getCurrencyInstance(new Locale("es", "ES")); // España = formato europeo
        euroFormat.setCurrency(Currency.getInstance("EUR"));
        tvVentasHoy.setText(euroFormat.format(data.getVentasHoy()));
        tvProductosTotal.setText(String.valueOf(data.getTotalProductos()));
        tvStockBajo.setText(String.valueOf(data.getStockBajo()));

        // Gráfico de barras
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < data.getVentas7Dias().size(); i++) {
            entries.add(new BarEntry(i, data.getVentas7Dias().get(i).getCantidad()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barChart.setData(new BarData(dataSet));
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(
                data.getVentas7Dias().stream().map(VentaDia::getDia).toList()
        ));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getLegend().setEnabled(false);
        barChart.invalidate();

        // Top 5 productos
        layoutTop5.removeAllViews();
        int pos = 1;
        for (ProductoVendido p : data.getTop5Productos()) {
            TextView tv = new TextView(this);
            tv.setText(pos++ + ". " + p.getNombre() + " → " + p.getCantidad() + " und");
            tv.setTextSize(16);
            tv.setPadding(0, 8, 0, 8);
            layoutTop5.addView(tv);
        }

        layoutContent.setVisibility(View.VISIBLE);
    }
}