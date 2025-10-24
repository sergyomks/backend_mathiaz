package in.sisfacturacion.facturacion.io;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificarPagoRequest {
    private String mercadopagoOrderId;
    private String mercadopagoId;
    private String mercadopagoFirma;
    private String orderId;
}
