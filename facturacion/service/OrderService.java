package in.sisfacturacion.facturacion.service;

import in.sisfacturacion.facturacion.io.*;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    void deleteOrder(String orderId);
    List<OrderResponse> getLatestOrders();

    OrderResponse verificarPago(VerificarPagoRequest request);
    Double sumVentasByDate(LocalDate date);
    Long countOrderByDate(LocalDate date);
    List<OrderResponse> findRecentOrders();
    List<VentasPorDiaResponse> getVentasUltimos14Dias();
    List<ProductoMasVendidoResponse>getProductosMasVendidos(int limit);
    List<VentasPorCategoriaResponse> getVentasPorCategoria();
    List<ComparacionAnualResponse> getComparacionAnual();
}
