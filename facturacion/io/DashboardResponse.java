package in.sisfacturacion.facturacion.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private Double ventasHoy;
    private Long totalPedidoHoy;
    private List<OrderResponse> recientesPedidos;
    // Ventas por Día - Últimos 14 Días (gráfico de línea)
    private List<VentasPorDiaResponse> ventasUltimos14Dias;
    // Productos Más Vendidos (gráfico de barras horizontal)
    private List<ProductoMasVendidoResponse> productosMasVendidos;
    // Ventas por Categoría (gráfico de barras vertical)
    private List<VentasPorCategoriaResponse> ventasPorCategoria;
    // Comparación Anual - Año Actual vs Anterior (gráfico de área)
    private List<ComparacionAnualResponse> comparacionAnual;
}
