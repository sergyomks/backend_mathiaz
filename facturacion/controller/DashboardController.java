package in.sisfacturacion.facturacion.controller;

import in.sisfacturacion.facturacion.io.*;
import in.sisfacturacion.facturacion.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final OrderService orderService;
    @GetMapping
    public DashboardResponse fetchDashboardData(){
        LocalDate today = LocalDate.now();
        Double ventasHoy = orderService.sumVentasByDate(today);
        Long totalPedidoHoy = orderService.countOrderByDate(today);
        List<OrderResponse>recientesPedidos = orderService.findRecentOrders();
        List<VentasPorDiaResponse> ventasUltimos14Dias= orderService.getVentasUltimos14Dias();
        List<ProductoMasVendidoResponse> productosMasVendidos = orderService.getProductosMasVendidos(5);
        List<VentasPorCategoriaResponse> ventasPorCategoria = orderService.getVentasPorCategoria();
        List<ComparacionAnualResponse> comparacionAnual = orderService.getComparacionAnual();
        return DashboardResponse.builder()
                .ventasHoy(ventasHoy != null ? ventasHoy : 0.0)
                .totalPedidoHoy(totalPedidoHoy != null ? totalPedidoHoy : 0)
                .recientesPedidos(recientesPedidos)
                .ventasUltimos14Dias(ventasUltimos14Dias)
                .productosMasVendidos(productosMasVendidos)
                .ventasPorCategoria(ventasPorCategoria)
                .comparacionAnual(comparacionAnual)
                .build();
    }
}
