package in.sisfacturacion.facturacion.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private String orderId;
    private  String clienteNombre;
    private String numeroDni;
    private List<OrderResponse.OrderItemResponse> items;
    private Double subTotal;
    private Double impuesto;
    private Double grandTotal;
    private MetodoPago metodoPago;
    private LocalDateTime fechaCreacion;
    private DetallePago detallePago;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderItemResponse{
        private String itemId;
        private String nombre;
        private Double precio;
        private String talla;
        private Integer quantity;
    }
}
