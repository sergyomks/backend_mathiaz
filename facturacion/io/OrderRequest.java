package in.sisfacturacion.facturacion.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {
    private  String clienteNombre;
    private String numeroDni;
    private List<OrderItemRequest> cartItems;
    private Double subTotal;
    private Double impuesto;
    private Double grandTotal;
    private String metodoPago;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderItemRequest{
        private String itemId;
        private String nombre;
        private Double precio;
        private String talla;
        private Integer quantity;
    }
}