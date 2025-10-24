package in.sisfacturacion.facturacion.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MercadoPagoOrderResponse {
    private String id;
    private String entity;
    private String status; // Mejor que "estados"
    private BigDecimal amount; // Usar BigDecimal para dinero
    private String currency; // "moneda" -> "currency"
    private Date createdAt; // "fechaCreacion"
    private String externalReference; // Tu orderId
    private String initPoint; // URL para redirigir al pago
}
