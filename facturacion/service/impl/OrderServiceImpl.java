package in.sisfacturacion.facturacion.service.impl;

import in.sisfacturacion.facturacion.entity.OrderEntity;
import in.sisfacturacion.facturacion.entity.OrderItemEntity;
import in.sisfacturacion.facturacion.io.*;
import in.sisfacturacion.facturacion.repository.OrderEntityRepository;
import in.sisfacturacion.facturacion.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderEntityRepository orderEntityRepository;
    @Override
    public OrderResponse createOrder(OrderRequest request) {
        OrderEntity newOrder = convertToOrderEntity(request);
        DetallePago detallePago = new DetallePago();
        detallePago.setEstado(newOrder.getMetodoPago()== MetodoPago.DINERO ?
                 DetallePago.EstadoPago.COMPLETADO: DetallePago.EstadoPago.PENDIENTE);
        newOrder.setDetallePago(detallePago);
        List<OrderItemEntity> orderItems=request.getCartItems().stream()
                .map(this::convertToOrderItemEntity)
                .collect(Collectors.toList());
        newOrder.setItems(orderItems);
        newOrder= orderEntityRepository.save(newOrder);
        return convertToResponse(newOrder);
    }

    private OrderItemEntity convertToOrderItemEntity(OrderRequest.OrderItemRequest orderItemRequest) {
        return OrderItemEntity.builder()
                .itemId(orderItemRequest.getItemId())
                .nombre(orderItemRequest.getNombre())
                .precio(orderItemRequest.getPrecio())
                .talla(orderItemRequest.getTalla())
                .quantity(orderItemRequest.getQuantity())
                .build();
    }

    private OrderResponse convertToResponse(OrderEntity newOrder) {
        return OrderResponse.builder()
                .orderId(newOrder.getOrderId())
                .clienteNombre(newOrder.getClienteNombre())
                .numeroDni(newOrder.getNumeroDni())
                .subTotal(newOrder.getSubTotal())
                .impuesto(newOrder.getImpuesto())
                .grandTotal(newOrder.getGrandTotal())
                .metodoPago(newOrder.getMetodoPago())
                .items(newOrder.getItems().stream()
                        .map(this::convertToItemResponse)
                        .collect(Collectors.toList()))
                .detallePago(newOrder.getDetallePago())
                .fechaCreacion(newOrder.getFechaCreacion())
                .build();
    }

    private OrderResponse.OrderItemResponse convertToItemResponse(OrderItemEntity orderItemEntity) {
        return OrderResponse.OrderItemResponse.builder()
                .itemId(orderItemEntity.getItemId())
                .nombre(orderItemEntity.getNombre())
                .precio(orderItemEntity.getPrecio())
                .quantity(orderItemEntity.getQuantity())
                .talla(orderItemEntity.getTalla())
                .build();
    }

    private OrderEntity convertToOrderEntity(OrderRequest request) {
        return OrderEntity.builder()
                .clienteNombre(request.getClienteNombre())
                .numeroDni(request.getNumeroDni())
                .subTotal(request.getSubTotal())
                .impuesto(request.getImpuesto())
                .grandTotal(request.getGrandTotal())
                .metodoPago(MetodoPago.valueOf(request.getMetodoPago()))
                .build();
    }

    @Override
    public void deleteOrder(String orderId) {
        OrderEntity existingOrder=orderEntityRepository.findByOrderId(orderId)
                .orElseThrow(()->new RuntimeException("pedido no encontrado"));
        orderEntityRepository.delete(existingOrder);
    }

    @Override
    public List<OrderResponse> getLatestOrders() {
        return orderEntityRepository.findAllByOrderByFechaCreacionDesc()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse verificarPago(VerificarPagoRequest request) {
        OrderEntity existingOrder=orderEntityRepository.findByOrderId(request.getOrderId())
                .orElseThrow(()->new RuntimeException("pedido no encontrado"));
        if (!verificarMercadopagoFirma(request.getMercadopagoOrderId(),request.getMercadopagoId(),request.getMercadopagoFirma())) {
            throw new RuntimeException("verificacion de pago fallido");
        }
        DetallePago detallePago = existingOrder.getDetallePago();
        detallePago.setMercadopagoOrderId(request.getMercadopagoOrderId());
        detallePago.setMercadopagoId(request.getMercadopagoId());
        detallePago.setMercadopagoFirma(request.getMercadopagoFirma());
        detallePago.setEstado(DetallePago.EstadoPago.COMPLETADO);

        existingOrder=orderEntityRepository.save(existingOrder);
        return convertToResponse(existingOrder);
    }

    @Override
    public Double sumVentasByDate(LocalDate date) {
        return orderEntityRepository.sumVentasByDate(date);
    }

    @Override
    public Long countOrderByDate(LocalDate date) {
        return orderEntityRepository.countVentasByDate(date);
    }

    @Override
    public List<OrderResponse> findRecentOrders() {
        return orderEntityRepository.findRecentOrders(PageRequest.of(0,5))
                .stream()
                .map(orderEntity -> convertToResponse(orderEntity))
                .collect(Collectors.toList());
    }

    @Override
    public List<VentasPorDiaResponse> getVentasUltimos14Dias() {
        List<VentasPorDiaResponse> ventasPorDia=new ArrayList<>();
        LocalDate fechaFin = LocalDate.now();
        LocalDate fechaInicio = fechaFin.minusDays(13); // 14 días incluyendo hoy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        for (LocalDate fecha = fechaInicio; !fecha.isAfter(fechaFin); fecha = fecha.plusDays(1)) {
            Double ventasDelDia = orderEntityRepository.sumVentasByDate(fecha);

            VentasPorDiaResponse ventasDia = VentasPorDiaResponse.builder()
                    .fecha(fecha)
                    .ventas(ventasDelDia != null ? ventasDelDia : 0.0)
                    .fechaFormateada(fecha.format(formatter))
                    .build();

            ventasPorDia.add(ventasDia);
        }
        return ventasPorDia;
    }

    @Override
    public List<ProductoMasVendidoResponse> getProductosMasVendidos(int limit) {
        return orderEntityRepository.findProductosMasVendidos(PageRequest.of(0, limit));
    }

    @Override
    public List<VentasPorCategoriaResponse> getVentasPorCategoria() {
        return orderEntityRepository.findVentasPorCategoria();
    }

    @Override
    public List<ComparacionAnualResponse> getComparacionAnual() {
        int anoActual = LocalDate.now().getYear();
        int anoAnterior = anoActual - 1;

        List<Object[]> resultados = orderEntityRepository.findVentasPorMesYAno(anoActual, anoAnterior);

        // Crear un mapa para organizar los datos por mes
        java.util.Map<Integer, ComparacionAnualResponse> mapaComparacion = new java.util.HashMap<>();

        // Inicializar todos los meses con valores en 0
        String[] nombresMeses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        for (int mes = 1; mes <= 12; mes++) {
            mapaComparacion.put(mes, ComparacionAnualResponse.builder()
                    .mes(mes)
                    .mesNombre(nombresMeses[mes - 1])
                    .ventasAnoActual(0.0)
                    .ventasAnoAnterior(0.0)
                    .build());
        }

        // Llena los datos reales
        for (Object[] row : resultados) {
            Integer ano = (Integer) row[0];
            Integer mes = (Integer) row[1];
            Double total = (Double) row[2];

            ComparacionAnualResponse comparacion = mapaComparacion.get(mes);
            if (ano == anoActual) {
                comparacion.setVentasAnoActual(total != null ? total : 0.0);
            } else if (ano == anoAnterior) {
                comparacion.setVentasAnoAnterior(total != null ? total : 0.0);
            }
        }

        // Convertir el mapa a lista ordenada por mes
        return mapaComparacion.values().stream()
                .sorted((a, b) -> a.getMes().compareTo(b.getMes()))
                .collect(Collectors.toList());
    }

    private boolean verificarMercadopagoFirma(String mercadopagoOrderId, String mercadopagoId, String mercadopagoFirma) {
        return true;
    }
}
